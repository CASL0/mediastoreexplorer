---
name: android-kb
description: Android の API・推奨パターン・互換性・非推奨状況・必要権限など「開発者が Android について調べたいこと」を `android docs` CLI（公式ナレッジベース）で必ず一次調査してから返答するスキル。「〜の使い方」「〜って何」「〜は使える？」「〜は deprecated？」「いつから対応？」「ベストプラクティス」「公式ドキュメント」「最新の方法」「API リファレンス」「権限は？」「minSdk いくつ？」「Android docs」など、Android プラットフォームの仕様・API・パターンに関する質問のあらゆる語彙で発火する。対象 API 名（MediaStore / ContentResolver / Photo Picker / WorkManager / DataStore / Compose Navigation / Foreground Service / Notification / Permission / ActivityResultContracts など）が文脈に出た時点で発火する。コードを書く前ではなく "知りたい" 段階のあらゆる質問でこのスキルを使うこと。学習データの曖昧な記憶や Web 検索ではなく、必ず最初に `android docs search` を叩く。
---

# Android KB 調査スキル

`android docs` CLI（厳選された Android 公式ナレッジベース）を一次ソースにして、API・パターン・互換性・注意点を調べるスキル。**学習データに頼った推測回答を防ぎ、最新の公式情報で答えること** が目的。

実装計画を立てる必要があるなら `android-implementation-planner` を、コードを書くなら `android-implementation-executor` を使う。このスキルは "調べて伝える" だけを担当する。

## なぜ CLI を必ず使うのか

Android プラットフォームは API レベルごとに以下が頻繁に変わる:

- 推奨パターン（例: 写真選択は `ActivityResultContracts.PickVisualMedia` が現行推奨、`ACTION_OPEN_DOCUMENT` や独自 MediaStore クエリは旧パターン）
- 必要権限（`READ_EXTERNAL_STORAGE` → `READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO` / `READ_MEDIA_VISUAL_USER_SELECTED`）
- API の非推奨化・削除
- 必要 minSdk・targetSdk と互換ライブラリ

LLM の学習データは時点が古く、しかも公式ドキュメントとブログ記事が混在している。`android docs` CLI は Google が厳選した KB を引くため、Web 学習データの曖昧な記憶よりも信頼できる。これが "必ず CLI を最初に叩く" 理由。

## ワークフロー

### 1. 質問を 1〜3 個の検索クエリに翻訳する

ユーザー質問から、調べるべきトピック・API 名・概念を抜き出す。1 回の検索で取りこぼす可能性があるので、観点を変えて 1〜3 クエリ用意する。

例:
- 質問: 「画像を選ばせる UI、今のおすすめは？」
  - クエリ 1: `photo picker`
  - クエリ 2: `PickVisualMedia ActivityResultContracts`
  - クエリ 3: `READ_MEDIA_VISUAL_USER_SELECTED`

### 2. `android docs search` を叩く

```
android docs search "<query>"
```

`kb://...` 形式の URL が番号付きで返る。タイトルと要約から関連性の高いものを 1〜3 件選ぶ。

### 3. `android docs fetch` で本文を取得する

```
android docs fetch "kb://android/..."
```

Markdown 形式で本文・サンプルコード・互換性表が返る。本文中の "Android X からは…" "deprecated in API Y" "requires API Z" のような互換性に関わる記述に必ず目を通す。

### 4. 必要なら検索クエリを言い換えて追加調査する

最初の検索で目的の情報が出ない場合、最大 3 回まで言い換えて再検索する。例: API の正式名がわからないときは挙動から検索（「foreground service notification mandatory」→「foreground service types」）。

### 5. WebFetch は最終手段

以下の場合のみ WebFetch を使う:

- `android docs search` を 3 回言い換えても適切な KB が返らない
- トピックが KB の範囲外（AOSP 内部実装、サードパーティライブラリの詳細、ブログのみのリリース情報）

WebFetch を使う場合も `developer.android.com`、`android.googlesource.com`、AndroidX 公式ドキュメントを Stack Overflow / Medium より優先する。

## 出力テンプレート

調査結果は **必ず以下の構成** で返す。各セクションは KB の出典に基づいて記述すること（推測で埋めない）。

```markdown
## 概要
（API・概念・パターンの 2〜3 文要約。何のためのものか、どこで使うか）

## 使い方 / 推奨パターン
- 主要な呼び出しステップ
- 必要に応じて短いサンプルコード（KB 内のコードを引用、独自に書き起こさない）
- 現行推奨されるイディオム

## 注意点
- 必要 API レベル（min / target）と挙動の差
- 非推奨ステータス（いつから deprecated か、代替 API は何か）
- 必要権限とランタイム要求のタイミング
- スレッディング・ライフサイクル・コルーチン関連の制約
- 既知の罠・互換性挙動・よくある間違い

## 出典
- kb://android/...（取得した URL）
- kb://android/...
```

「注意点」は **空で済ませない**。必要 API レベル・非推奨情報・権限のいずれかは KB を読めば必ず何か出てくる。本当に何もない場合のみ「特になし」と書く。

## 範囲外（このスキルでやらないこと）

- **コードを書く** → `android-implementation-executor` の仕事
- **プロジェクト固有の build.gradle.kts と照合して採否を判断する** → `android-implementation-planner` の仕事
- **ライブラリのバージョンを上げる PR を作る** → 通常の編集ワークフロー

このスキルは「最新の事実を集めて整理する」だけ。実装に踏み込まない。

## 例

**質問**: "Photo Picker の使い方を教えて"

**やること**:
1. `android docs search "photo picker"`
2. 上位の `kb://android/training/data-storage/shared/photo-picker/index` を fetch
3. `READ_MEDIA_VISUAL_USER_SELECTED` の互換性ページも fetch
4. 上記テンプレートで、`PickVisualMedia` の使い方・Android 14 の Selected Photos Access・必要権限・対応開始 API レベルをまとめて返す
