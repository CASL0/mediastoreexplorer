---
name: android-implementation-executor
description: mediastoreexplorer プロジェクト（Kotlin / Jetpack Compose）で android-implementation-planner が出した「実装計画」をステップバイステップに実装し、論理単位ごとに Conventional Commits で記録するスキル。「実装して」「実装を進めて」「計画通りにコードを書いて」「実装開始」「let's implement」「go ahead and code」など計画の実行・コード化を含意するあらゆる語彙で必ず発火する。Phase ごとに spotlessApply / assembleDebug / testDebugUnitTest / detekt を回し、`docs/git.md` のブランチ・コミット規約と `docs/architecture.md` のレイヤー構造、`docs/security.md` の MASVS コントロールを毎回照合する。「とりあえず書く」前に必ずこのスキルを呼ぶこと。
---

# Android 実装実行スキル

`android-implementation-planner` が出した実装計画を起点に、mediastoreexplorer プロジェクトのコードを **論理単位ごとにコミットしながら** 進めるスキル。設計判断は計画書側で済ませ、本スキルは「実装・検証・コミット」の機械的かつ厳密なループに徹する。

## このスキルが存在する理由

計画があってもなくても実装は手で書ける。だが以下の失敗が頻発する:

- ブランチを切らずに `main` で直接コミットしてしまう
- spotless の自動 import 削除を踏み、import だけ先に追加して消されたまま実装する
- テスト未実行のままコミットを重ね、`git bisect` が壊れる
- 既存の対称構造（`images/` `videos/` `audios/` `downloads/`）と異なる流儀で書き、レビューでの差し戻しが増える
- セキュリティ観点（MASVS）を後付けで思い出す
- コミットメッセージが Conventional Commits に従わず、semantic-release のリリース判定を狂わせる

これらは「計画の質」ではなく「実行の規律」の問題。本スキルはその規律を毎ステップで強制する。

## このスキルが前提にすること

- **承認済みの実装計画が会話文脈に存在する**こと（`android-implementation-planner` の出力末尾に `WAITING FOR CONFIRMATION` があり、ユーザーが `yes` または同等の承認をしている）
- 計画がないなら、まず `android-implementation-planner` を呼んで計画を立てるよう案内し、本スキルは停止する

計画文脈なしに着手しようとしてはいけない。プロジェクト規約上、計画段階で SDK 互換性・非推奨 API・パターン適合を確定させる前に書いたコードはほぼ確実に手戻りする。

## ワークフロー

### 0. 事前確認

以下のすべてが満たされていることを確認する。1 つでも欠けたら停止して案内する。

- [ ] 承認済み実装計画が会話文脈に存在する
- [ ] 計画書に「実装手順」セクション（Phase 群）がある
- [ ] 現在地が `c:\Users\ooshiro\ghq\github.com\CASL0\mediastoreexplorer` の作業ツリーである
- [ ] `git status` がクリーン、または現在のブランチが本実装専用である

加えて、計画 Issue の番号があれば確認しておく:

- `android-implementation-planner` が Step 9 で `gh issue create` を実行した場合、会話文脈に Issue 番号（`#<番号>`）が残っているはず
- 番号が見当たらないが計画 Issue は存在しそうという場合、`gh issue list --label enhancement --search "実装計画: <タイトル>"` で照合する
- 計画 Issue が立っていなければ、それ自体は致命ではない。ただし PR の `Closes #...` 行は省略する（存在しない Issue を参照しない）

未コミット変更がある場合の判断は以下:

- **計画と関連が明確な変更**（同じファイル群への手を入れた途中、計画上 Phase 1 で触る箇所への準備変更など）→ `git stash push -u -m "<context>"` を提案し、対象ブランチ作成後に `git stash pop` で復帰させる選択肢を出す
- **計画と関連が不明な変更**（別機能の作業途中、設定ファイル類の変更など）→ 中断してユーザーに返す。勝手に stash しない、勝手に破棄しない

判断に迷うときは中断側に倒す。失われたら困る変更を巻き込むほうが、ステップを 1 つ増やすより害が大きい。

### 1. ブランチを切る

`docs/git.md` のブランチ戦略（GitHub Flow）に従う。

- `main` を最新化してから新規ブランチを切る:

  ```bash
  git fetch origin
  git switch main
  git pull --ff-only origin main
  git switch -c <type>/<description>
  ```

- ブランチ名は `<type>/<description>` 形式。type は `docs/git.md` の表（`feat` / `fix` / `docs` / `refactor` / `chore` / `perf` / `ci` / `test` / `style`）から選ぶ。description はケバブケースで簡潔に（例: `feat/photo-picker`, `fix/permission-loop`, `refactor/datasource-extract`）

既に対象ブランチで作業中なら、新規作成はスキップしてその旨をユーザーに告げる。

### 2. Phase をコミット粒度の論理単位に分解する

計画書の Phase をそのまま 1 コミットにできるとは限らない。以下を判定基準として **論理単位** に切り直す:

- **1 コミット = 「ビルドが通り、テストが通る最小の論理変化」**（`docs/git.md` の「途中の壊れた状態をコミットしない」を厳守）
- 1 ファイルでも複数の意味が混ざるなら分割（例: API 追加とリファクタを混ぜない）
- spotless の import 自動削除を避けるため、**import の追加と使用コードの追加は同一 Edit で行う**（CLAUDE.md の Key Conventions）
- 関連テストは「対応する実装と同じコミット」に含める（CLAUDE.md の `testDebugUnitTest` がパスすることが各コミットの前提）

計画書はすでにユーザー承認済みなので、ここで切り直したリストをユーザーに見せて再承認は取らない。実装の最初のステップに入る前に **TodoWrite に各論理単位を 1 タスクとして登録** し、進捗を可視化する。これだけで「何をどの順で進めるか」がユーザーから見えるため、追加の停止プロンプトは不要。

ただし、計画書が想定していなかった追加コミットを途中で挿入したくなった場合（依存追加・テスト基盤の整備など）は、**勝手に増やさず Section 4 の逸脱フローに従って停止する**。

### 3. 各論理単位を実装する

以下を **1 単位ごとに繰り返す**。

#### 3.1 既存パターンを 1 つ読む

Repository 層なら `MediaRepositoryImpl.kt` と `datasource/ImageMediaDataSource.kt` を、UI 層なら `ui/images/ImagesViewModel.kt` と `ui/images/ImagesScreen.kt` を、テストなら `app/src/test/.../ImagesViewModelTest.kt` を必ず先に読む。**既存の対称構造（images / videos / audios / downloads の 4 タブ）に合わせる** ことがレビュー摩擦の最小化に直結する。

ただし以下のときは既存パターンに従わず、より新しい/推奨パターンを選んでよい（ユーザーの指針: 「非推奨なパターンなどであればその限りではない」）:

- 既存パターンが Android docs / Compose docs で deprecated とされている
- 計画書の検証で代替が推奨されている
- セキュリティ要件（後述 3.3）と矛盾する

逸脱する場合は、コミットメッセージ本文または PR で **理由を 1 行** 明示する。

#### 3.2 アーキテクチャ整合を確認する

`docs/architecture.md` のレイヤーに沿っているか機械的に照合する:

- **UI 層** — `Screen` は状態表示とイベント受け付けのみ。ロジックは `ViewModel`。`Context` は ViewModel に持ち込まない
- **データ層** — `Repository` インターフェースを介する。`MediaRepositoryImpl` は薄いアダプターに保ち、IO は **必ず** `withContext(@IoDispatcher)`
- **DataSource** — `ContentResolver.query()` → Cursor マッピングのみ。Cursor 拡張関数は `CursorExtensions.kt` を再利用
- **DI** — Hilt のコンストラクタインジェクション。Repository は `@Singleton`、IO Dispatcher は `@IoDispatcher`
- **状態の収集** — Compose では `collectAsStateWithLifecycle()`（`collectAsState()` ではない）

DataSource を追加・削除した場合は、**`androidTest/MediaRepositoryImplTest` のコンストラクター呼び出しも同じコミット内で更新する**（CLAUDE.md の明記事項）。

#### 3.3 セキュリティチェック

`docs/security.md` の中から、今のコミットに該当するコントロールだけを 30 秒で見る。該当がなければスキップして良いが、以下のキーワードに触れる変更は必ず該当章を再読する:

| 変更内容のキーワード | 確認すべき MASVS 章 | チェック観点 |
| --- | --- | --- |
| `SharedPreferences` / Room / ファイル書き込み / バックアップ | MASVS-STORAGE | 機密データを平文で書いていないか、`allowBackup`/`backup_rules.xml` に矛盾がないか、`Log.*` に機密情報が漏れていないか |
| 暗号化・ハッシュ・乱数 | MASVS-CRYPTO | `MD5/SHA-1/DES/RC4` を使っていないか、鍵を Keystore で管理しているか、乱数は `SecureRandom` か |
| Manifest 編集・`exported` 変更・WebView / Deep Link / Intent | MASVS-PLATFORM | 権限が最小か、`exported="true"` に intent-filter が限定されているか、Deep Link 入力を検証しているか、`setJavaScriptEnabled` の扱い |
| パスワード・トークン・API キー・生体認証 | MASVS-AUTH | 機密値をハードコードしていないか、`EncryptedSharedPreferences`/`Keystore` を使っているか、`BiometricPrompt` を使っているか |
| HTTP 通信・OkHttp/Retrofit・`network_security_config.xml` | MASVS-NETWORK | 平文 HTTP を許可していないか、TLS 検証をスキップしていないか |
| 依存ライブラリ追加・`libs.versions.toml` 編集 | MASVS-CODE | 既知脆弱性のないバージョンか、リリースビルドに影響しないか |
| 位置情報・連絡先・識別子・分析 SDK | MASVS-PRIVACY | 必要最小限のデータか、ユーザー説明と同意の経路があるか |

該当があれば、**該当行/該当ファイルでどのコントロールに従ったか** をコミットメッセージ本文に 1 行記す（例: `MASVS-PLATFORM-1: 受信 Intent の URI を ContentResolver でのみ解決するよう制限`）。

#### 3.4 実装する

- `Edit` を優先。ファイル新規作成は必要なときだけ
- 同一 Edit で **import + 使用コード** を追加（spotless の pre-commit が未使用 import を消すため）
- KDoc は **自分が変更したコード** にだけ追加し、自明でない情報のみ書く（CLAUDE.md）
- コメントは「なぜ」を書く。「リストを返す」のような自明な what は書かない
- `Context` を ViewModel に持ち込まない、IO は `withContext(@IoDispatcher)` に分離

#### 3.5 検証コマンドを順に実行する

各コマンドが **すべてグリーン** になるまで次へ進まない。失敗したらその場で原因を特定して修正してから再実行する。

```powershell
# 1. フォーマット自動修正（再実行で差分が消えるまで）
.\gradlew spotlessApply

# 2. ビルド
.\gradlew assembleDebug

# 3. 単体テスト（編集スコープに限定できるなら --tests で絞ってもよい）
.\gradlew testDebugUnitTest

# 4. 静的解析
.\gradlew detekt
```

検証で失敗したら:

- **assembleDebug 失敗** → エラーを最小再現で読む。レイヤー違反（例: ViewModel に Context 注入）を含んでいないか確認
- **testDebugUnitTest 失敗** → 該当テストを `--tests` で単体実行し、`MainDispatcherRule` / `FakeMediaRepository` の使い方が既存と一致しているかを確認
- **detekt 失敗** → ルール側で sane な指摘なら修正、構造的に難しいなら計画書側に立ち返る
- **spotless が毎回差分を出す** → 自分の Edit と spotless の整形が衝突している。spotless の整形に合わせて再 Edit

instrumented test（`connectedDebugAndroidTest`）は実機/エミュ必須なので **コミット前検証では走らせない**。必要時のみユーザーが手動で回す。CI でカバレッジ付きの `jacocoDebugCoverageReport` が走る前提。

#### 3.6 コミットする

`docs/git.md` の Conventional Commits に厳密に従う。

```
<type>[(scope)]: <description>

[optional body — なぜこの変更が必要か / 設計判断 / セキュリティ観点]

[optional footer — BREAKING CHANGE / Refs / 共著者]
```

実例（このプロジェクトの既存スタイル）:

- `feat(images): 画像一覧画面を追加`
- `fix(permission): 権限拒否後の再表示でループする問題を修正`
- `refactor(datasource): MediaRepositoryImpl から Cursor 解析を抽出`
- `chore(deps): androidx.compose.bom を 2026.x にアップデート`
- `test(videos): VideosViewModel の loading 状態テストを追加`

破壊的変更は `feat!: ...` または footer に `BREAKING CHANGE: <内容>`。`docs/git.md` の semver 表に従う。

コミット手順:

```bash
git status                        # 巻き込みファイル確認
git add <個別パス>                # `git add -A` / `git add .` は使わない
git diff --cached                 # 直前確認
git commit -m "<message>"         # spotless の pre-commit が走るので失敗時は再実行
```

コミット失敗時は **新しいコミットを作る**（`--amend` しない）。CLAUDE.md と Claude Code 既定方針に従う。

### 4. 残りの論理単位を順に処理する

3.1〜3.6 を、**Phase 全体** が終わるまで繰り返す。Phase 間で計画書を読み直し、前 Phase の影響で計画が変わっていないか確認する（特に依存追加・モジュール構成変更が後続に波及しているとき）。

計画書からの逸脱が発生した場合は、**逸脱した時点で停止してユーザーに報告する**。勝手に計画を書き換えない。報告フォーマット:

```
計画からの逸脱が発生しました:
- 計画: <該当 Phase の記述>
- 実態: <逸脱の内容>
- 原因: <検証で判明した制約>
- 提案: A) 計画修正して進む / B) 別アプローチ / C) ロールバック
どうしますか?
```

### 5. push と PR

全コミットが終わったら以下:

```bash
git push -u origin <branch>
gh pr create --base main --title "<PR タイトル>" --body "$(cat <<'EOF'
## Summary
- <1〜3 行のバレット>

## Changes
- <主要な変更点>

## Verification
- [x] ./gradlew assembleDebug
- [x] ./gradlew testDebugUnitTest
- [x] ./gradlew detekt
- [x] ./gradlew spotlessCheck
- [ ] ./gradlew connectedDebugAndroidTest（必要なら手動）

## Related plan
Closes #<計画 Issue 番号>
<実装計画の要点 or 計画書のセクション参照>

## Security notes
<MASVS コントロールへの該当があれば 1 行で>
EOF
)"
```

PR タイトルは Conventional Commits の 1 行目をそのまま使う形を推奨（squash merge 時にもメッセージ規約が保たれる）。

PR 本文の `Related plan` は次のルールで埋める:

- `android-implementation-planner` が Step 9 で立てた計画 Issue がある場合、先頭に `Closes #<番号>` を 1 行で書く。PR が squash merge されると GitHub 側で Issue が自動クローズされ、「計画 → 実装 → クローズ」が辿れる
- 計画 Issue が無い場合は `Closes` 行を省略し、計画の要点を 3〜5 行で要約する
- 既存 Issue を完全には解消しない部分実装の場合は `Refs #<番号>` を使い（自動クローズを避ける）、残作業を 1 行添える

### 6. 確認待ちで停止する

PR 作成後、URL をユーザーに伝えて停止する。

```
PR を作成しました: <URL>

このあとの選択肢:
- 追加レビュー指摘への対応
- `gh pr merge` での squash merge（main にマージ後、ローカルでブランチ削除）
- レビュー前に追加の Phase を実装
```

マージはユーザー判断で行う。本スキルから自動でマージしない。

## 出力テンプレート

ステップごとに以下のフォーマットでユーザーに状況を伝える（冗長にならないよう各ステップ短く）:

```
## ステップ N: <論理単位のタイトル>

### 既存パターン
- 参照: ImagesViewModel.kt（既存と同じ runCatching パターンを採用）

### アーキテクチャ整合
- UI 層 / データ層の責務に違反なし
- IO は @IoDispatcher で withContext

### セキュリティ
- 該当なし / MASVS-XXX-N: <一行>

### 実装
- <Edit / Write の概要>

### 検証
- spotlessApply: ✅
- assembleDebug: ✅
- testDebugUnitTest: ✅ (NN tests)
- detekt: ✅

### コミット
<type>(<scope>): <description>
```

最終ステップでは PR URL とサマリで締める。

## このスキルがやらないこと

- **計画立案はしない。** 計画は `android-implementation-planner` の領域。逸脱検知時もアプローチを書き換えるのは計画スキル
- **instrumented test を CI 前にローカルで走らせない。** 実機/エミュ前提のため、必要時はユーザーが手動で `connectedDebugAndroidTest` を回す
- **`main` への push / 強制 push / `--amend` / `--no-verify` をしない。** プロジェクト規約と Claude Code 既定方針に反する
- **マージしない。** PR を作って停止する
- **無関係な「ついで修正」を混ぜない。** 計画外の修正が必要だと気づいたら停止して報告。`docs/git.md` の「無関係な変更を1つのコミットに混ぜない」を厳守

## このスキルを読み込むモデルへのヒント

- spotless の pre-commit フックは Edit と衝突しやすい。**import の追加と使用コードの追加は同一 Edit にする**。最初に import だけ書いて次の Edit でコードを書くと、間に走る spotless が import を消す
- 既存の 4 タブ構造（images / videos / audios / downloads）は **対称性** が強み。新タブや新 DataSource を足す場合は、既存 4 つのうち最も近いものをそのまま雛形にする方が、ゼロから設計するより早く確実
- `androidTest/` と `test/` で `FakeMediaRepository` が **二重定義** されているのは別 source set のためで、片方だけ修正すると CI で破綻する。同期して直す
- `MediaRepositoryImplTest` を編集する場合、5 DataSource + `ioDispatcher` のコンストラクター呼び出しが正しいか目視で必ず確認（CLAUDE.md の明示警告）
- ユーザーが「とりあえず動けばいい」「計画はいらない」と言っても、計画なしの着手は本スキルの前提を欠く。素直に `android-implementation-planner` から始めるよう案内する
- PR 作成失敗（権限なし / `gh` 未設定 / リモート未追加）はユーザーに必要設定を伝えて停止。本スキルから設定をいじらない
