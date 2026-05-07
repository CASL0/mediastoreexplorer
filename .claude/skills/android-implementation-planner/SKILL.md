---
name: android-implementation-planner
description: mediastoreexplorer プロジェクト（Kotlin / Jetpack Compose）で Android 機能の実装計画・設計・リファクタリング方針・ライブラリ差し替えなどを検討するときに必ず使用するスキル。「実装計画」「設計」「どう実装」「やり方」「approach」「機能追加」「変更したい」など計画を含意するあらゆる語彙、および Activity / Fragment / Compose / MediaStore / 権限 / Hilt / Room / DataStore / WorkManager / Coil に触れる変更要請で発火する。`android docs` CLI で公式ドキュメントを参照し、プロジェクトの compileSdk / minSdk と照合してから API を選定するため、非自明な Android 変更を始める前に必ず呼び出すこと。
---

# Android 実装計画スキル

mediastoreexplorer プロジェクト（Kotlin + Jetpack Compose）で Android 機能を実装する前に、`android docs` CLI で公式ドキュメントを参照し、プロジェクトの SDK 制約と照合した実装計画を立てるスキル。コードを書く前の段階で「API ミスマッチ」「非推奨呼び出し」「古いパターン」を捕まえることが目的。

## このスキルが存在する理由

Android の実装計画は、紙の上では妥当に見えても以下の理由で実装段階で詰まることが多い:

- 採用しようとした API が要求する `minSdk` がプロジェクトの値より高い
- API がモデルの学習データ以降に非推奨化／置き換えされている
- 2026 年現在「正しい」Compose のパターンが、モデルが最初に思いつくものと違う
- 権限・ライフサイクル・スレッディング制約はコードを書き始めるまで表面化しない

`android docs search` は厳選された公式 KB を引くため、Web 学習データの曖昧な記憶よりも信頼できる。これを最初に引くことが、このスキルの存在意義。

## ワークフロー

### 1. プロジェクトの SDK 制約をまず読む

プロジェクトルートの `app/build.gradle.kts` を開いて以下を取得する:

- `compileSdk` — AGP 9.x の特殊形式に注意: `release(36) { minorApiLevel = 1 }` ⇒ 実効コンパイル対象は API 36.1
- `minSdk`（現状 **24**）
- `targetSdk`（現状 **36**）
- Java target / Kotlin JVM target（現状 **JVM 11**）
- Compose 有効か（はい）
- 利用中プラグイン（Hilt, KSP, jacoco）

これらの値を計画書の冒頭に必ず引用する。以降のすべての API 選定はこれらと照合する。

### 2. 要件を言い換えて再確認する

ユーザーの依頼を具体的な言葉で言い直す。曖昧な点があっても、計画の構造を変えるレベルでなければ自分で仮定を置いて進めて構わない。ただしその仮定は計画内で明示する。

### 3. 候補となる API・パターンを列挙する

非自明な決定ごとに、検討対象を並べる。例:

- 「写真ピッカーを出す」→ `ActivityResultContracts.PickVisualMedia` vs `ACTION_OPEN_DOCUMENT` vs MediaStore クエリ
- 「バックグラウンド同期」→ `WorkManager` vs `JobScheduler` vs Foreground Service
- 「ローカルデータ」→ `DataStore` vs `Room` vs `SharedPreferences`
- 「画像読み込み」→ `Coil 3` vs `Glide` vs 自前 `BitmapFactory`

### 4. 各候補を `android docs` で検証する

各候補について、まず検索する:

```
android docs search "<トピックまたは API 名>"
```

`kb://...` 形式の URL が番号付きで返るので、関連性の高いものを 1〜3 件選んで取得する:

```
android docs fetch kb://android/develop/...
```

取得した内容から、**すべての候補について網羅的に**（「網羅して」という要件はここを指す）以下を確認する:

- **要求される最低 API レベル**
- **非推奨ステータス**（API X で deprecated? 代替 Y は何? 削除予定?）
- **推奨される使い方**（サンプルコード、ライフサイクル制約）
- **必要な権限**と要求フロー
- **スレッディング / コルーチン上の指針**

**WebFetch は最終手段** とする。次のいずれかに該当する場合のみ使う:

- `android docs search` で 2〜3 回クエリを言い換えても適切なものが返らない
- トピックが Android 公式 KB の範囲外（サードパーティ詳細、AOSP ソース、ブログのみのリリースノート）

KB は厳選されている。Web 検索のドリフトこそがこのスキルが防ぎたい失敗モード。フォールバック時も `developer.android.com`、`android.googlesource.com`、公式ライブラリのドキュメントサイトを Stack Overflow / Medium より優先する。

### 5. プロジェクト SDK と互換性を判定する

計画で使うすべての API について以下のように分類する:

- **API minSdk ≤ 24** → そのまま利用可。「API X から利用可（プロジェクト minSdk = 24）」と書く
- **API minSdk > 24** → 以下 3 オプションのいずれかを選び、**非機能要件への影響を必ず明示する**

  | 選択肢 | 何が変わるか | 明示すべき非機能影響 |
  |---|---|---|
  | バックポート / AndroidX 等価物 | 旧バージョン対応を Jetpack ライブラリに任せる | アプリサイズ増分、旧 OS での挙動パリティ、推移依存の追加 |
  | 条件分岐（`Build.VERSION.SDK_INT`） | ランタイムチェック越しに 2 実装を併存 | テスト面積の倍化、実装乖離リスク、コード複雑度 |
  | `minSdk` 引き上げ | 古い Android のサポートを切る | リーチ損失（Android Distribution dashboard を引用できれば理想）、Play Store 影響、レガシー経路の削除可能性 |

明確な推奨があってもこの 3 択を必ず提示する。最終決定はユーザーに任せる。

### 6. Compose / Kotlin パターンを docs と照合する

Compose / コルーチン / ライフサイクル系の決定は、概念名で個別に検索して現行ガイダンスと照合する:

- **State hoisting** — 提案している状態モデルが推奨どおりか
- **Side effects** — `LaunchedEffect`, `DisposableEffect`, `produceState` の使い方が現行 docs と一致するか
- **Recomposition** — `remember`, `derivedStateOf`, stable 型, `key()` の配置
- **コルーチン context** — IO 重い処理を Main スレッドから外す? `withContext(ioDispatcher)` を使う?
- **Lifecycle 連動** — `repeatOnLifecycle`, `collectAsStateWithLifecycle` を使う?
- **権限ハンドリング** — プロジェクト規約の `rememberLauncherForActivityResult` か?

これらは「2023 年時点で正しかったパターン」が古びている可能性が高い箇所。

### 7. リスクと未解決事項を明示する

検証後に以下を出す:

- **リスク**（HIGH / MEDIUM / LOW）と各 1 行の対策
- **オープンクエスチョン** — 実装着手前にユーザーが答えるべき項目
- **仮定** — ユーザーが指定しなかった点について自分が置いた仮定

### 8. 確認待ちで停止する

末尾で以下を出して停止する:

```
**WAITING FOR CONFIRMATION**: この計画で進めますか？ (yes / modify: <修正内容> / different approach: <代替案>)
```

ユーザーが明示的に承認するまでコード実装に入らない。

## 出力テンプレート

以下の構成で出力する（このプロジェクトのユーザーは日本語で作業しているので日本語で書く）:

```
# 実装計画: <タイトル>

## プロジェクト前提
- compileSdk: 36.1 (AGP 9.x の `release(36) { minorApiLevel = 1 }`)
- minSdk: 24
- targetSdk: 36
- Kotlin / JVM: 11 / Compose 有効 / Hilt + KSP

## 要件の再確認
- <要件1>
- <要件2>

（前提・仮定: <ユーザー未指定の点をここで明示>）

## 採用 API・パターン
| 用途 | 採用 | 代替案（不採用理由） | 必要 minSdk | 非推奨? | 出典 |
|------|------|----------------------|-------------|---------|------|
| ... | `androidx.foo.Bar` | `LegacyXxx` (API 30 で deprecated) | 21 | No | kb://android/... |

## minSdk 互換性チェック
<API ごとに「直接利用可」「条件分岐必要」「minSdk 引き上げ要」を判定。
引き上げや条件分岐が必要な場合は非機能要件への影響（リーチ、APK サイズ、UX パリティ、テスト工数）も書く>

## Compose / Kotlin パターン確認
- State hoisting: <docs と一致 / 修正提案>
- Side effects: ...
- Coroutine dispatch: <`@IoDispatcher` で `withContext` していること>
- Lifecycle: ...

## 実装手順
### Phase 1: <名前>
- 編集ファイル: `app/src/main/.../Foo.kt`, `app/src/test/.../FooTest.kt`
- 主要な変更: <短く>

### Phase 2: ...

## 依存追加 / 削除
- 追加: `androidx.xxx:yyy:1.2.3` （`gradle/libs.versions.toml` に追加）
- 削除: ...

## リスク
- HIGH: <内容> → 対策: <一行>
- MEDIUM: ...
- LOW: ...

## 参照したドキュメント
- [タイトル](kb://android/...) — 確認した内容を一行
- ...

**WAITING FOR CONFIRMATION**: この計画で進めますか？ (yes / modify: <内容> / different approach: <代替案>)
```

## 例

### 例 1: 写真ピッカー導入

ユーザー: 「写真を選んでアップロードする画面を追加したい」

Step 1 — `app/build.gradle.kts` で minSdk=24, compileSdk=36, Compose 有効を確認。

Step 4 — `android docs search "photo picker"` で `kb://android/training/data-storage/shared/photopicker` と `kb://android/reference/androidx/activity/result/contract/ActivityResultContracts.PickVisualMedia` を取得。

Step 4 — それぞれ `android docs fetch` して以下を確認:

- "Available on devices that run Android 13 (API 33) or higher"
- "On older devices, use `ActivityResultContracts.PickVisualMedia` which delegates to a permission-less photo picker via `androidx.activity` 1.7+"
- 推奨型: `PickVisualMediaRequest(PickVisualMedia.ImageOnly)`

Step 5 — 互換性判定: 直接の Photo Picker は API 33+ だが、`androidx.activity:activity:1.7+` 経由で API 21 まで自動フォールバック → **直接利用可**。**非機能影響**: 既に `activity-compose` 依存があるため追加コストなし、UX は Android 11+ / Play Services 上ではほぼ同等。

→ 計画では `rememberLauncherForActivityResult` + `PickVisualMedia` を採用、両 KB URL を引用する。

### 例 2: アップロード前の画像圧縮処理

ユーザー: 「アップロード前に画像を圧縮して送信する処理を追加して」

`android docs search "WorkManager"`, `"background tasks"`, `"CoroutineWorker"` を実行。

検証ポイント:

- `WorkManager` 自体の minSdk は 21 → 24 から問題なく利用可
- `CoroutineWorker` は推奨パターンとして現存（非推奨ではない）
- 短時間処理なので Foreground service は不要、one-time `WorkRequest` で十分と docs が示唆

→ 計画で `CoroutineWorker` + `OneTimeWorkRequestBuilder` を採用、IO 処理は `@IoDispatcher` 経由で `withContext` に分離（プロジェクト規約と一致）。

## このスキルがやらないこと

- **コードを書かない。** 計画のみ。実装への移行はユーザー承認後、`/tdd` などで行う
- **ビルドやテストは実行しない。** 「Phase 2 後に `./gradlew testDebugUnitTest` で検証推奨」のような提案はしてよい
- **Android 以外の関心事は対象外**（CI ワークフロー設計・ストアポリシー・マーケティング等）。ユーザーが明示的に依頼した場合を除く
- **1 つの選択肢に固定しない。** トレードオフがある場合（特に minSdk 判断）は選択肢を提示し、決定はユーザーに委ねる

## このスキルを読み込むモデルへのヒント

- 初回の `android docs search` は ~18 MB の KB を 1 度だけダウンロードする。プログレスバーが出ても焦らない
- `kb://android/...` URL はブラウザでは開けない。`android docs fetch` 専用。計画書には引用形式でそのまま載せると、CLI を持つ読者が再取得できる
- ユーザーの依頼が docs の明示的非推奨に当たる場合（例: MediaStore の便利 API があるのに生 `ContentResolver` で書きたいと言われた等）は、黙って従わずに計画書で旗を立てる
- 関係ないセクションで計画を水増ししない。1 ファイルだけの UI 微調整に「リスク」を 3 つも捻り出さない
