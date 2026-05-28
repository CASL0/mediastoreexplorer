---
name: compose-state-hoisting
description: Jetpack Compose の状態ホイスティング（state hoisting）の置き場所を監査し、誤った位置にある状態を正しい位置へ動かすスキル。「ホイスティング」「state hoisting」「状態の置き場所」「この state は ViewModel に上げるべき？」「ここで remember すべき？」「状態が散らかってる」「stateless にしたい」「UDF」「再利用しやすくしたい」「Composable のリファクタ」「プレビュー/テストしづらい」「property drilling」「ViewModel に何でも入ってる」など、Composable 内 / 状態ホルダー / ViewModel のどこに状態を置くかを再検討するあらゆる語彙で必ず発火する。`@Composable` 関数を読み書き・新規追加・リファクタしようとした時点で発火を検討し、その状態が現在の位置にある理由が言語化できないなら必ずこのスキルを使うこと。`android docs` CLI で State Hoisting / Thinking in Compose / UDF の公式ガイドを毎回参照してから判定し、ホイスティング不足（上げ忘れ）とホイスティング過多（無駄な ViewModel 露出）の両方向を直す。
---

# Compose State Hoisting 監査 & 修正スキル

Jetpack Compose の状態を「**最も近い共通祖先 (lowest common ancestor, LCA)**」に置くという公式ベストプラクティスを基準に、コードベースの Composable を**監査して、必要なら動かす**スキル。

## 目的

「とりあえず ViewModel に置く」「とりあえず remember しておく」をやめ、各 state を「**実際に読み書きする全 Composable の LCA**」に置く。これが満たされていない箇所を特定し、両方向に修正する:

- **ホイスティング不足**: 内部に閉じているが、別 Composable や ViewModel が読み書きしたい state → 親 / 状態ホルダー / ViewModel に上げる
- **ホイスティング過多**: ViewModel や親に上げているが、実際は 1 つの Composable しか使わない・ビジネスロジックも絡まない state → 内部 (`remember` / `rememberSaveable`) に降ろす

## まず `android docs` を引く（毎回）

判定の根拠を学習データの記憶ではなく公式 KB に固定する。最低限以下 3 ページを fetch して読む。トピックが曖昧なら 1〜3 クエリ追加で言い換える。

```
android docs search "state hoisting compose"
android docs fetch  "kb://android/develop/ui/compose/state-hoisting"
android docs fetch  "kb://android/develop/ui/compose/architecture"   # UDF
android docs fetch  "kb://android/develop/ui/compose/state"          # State APIs
```

必要に応じて補強で引くページ:

- `kb://android/develop/ui/compose/state-saving`   — `rememberSaveable` / `Saver`
- `kb://android/develop/ui/compose/state-lifespans` — `remember` / `rememberSaveable` / `retain` の使い分け
- `kb://android/develop/ui/compose/side-effects`   — `LaunchedEffect` / `DisposableEffect` のスコープ
- 個別 API（`PermissionState`, `LazyListState`, `DrawerState`, `TextFieldState` など）の現行推奨

学習データに頼った判定はしない。「現行ドキュメントではこうなっている」だけを判定材料にする。

## 判定ルール（KB から抽出）

KB をフェッチしたうえで、対象 state について以下を順に確認する。**KB の表現が変わっていたら KB を優先**し、このスキルの記述は古いものとして扱う。

### 1. その state は誰が読み、誰が書く？

- **書く側 (writer)**: `state = ...` / `state.value = ...` / `onXxx { ... state.something = ... }`
- **読む側 (reader)**: `state` を if 条件・パラメータ・`derivedStateOf` などで参照している箇所すべて

書き手 1 つ・読み手 1 つで、その Composable の外に出る予定もないなら**内部に閉じる**ことが許される（KB の "No state hoisting needed" 節）。

### 2. UI ロジックだけ？ ビジネスロジックも絡む？

KB の定義:

- **UI ロジック**: UI 状態を「どう表示するか」。スクロール、開閉、フォーカス、ヒントなど。Composition のライフサイクルに紐づく。
- **ビジネスロジック**: アプリの永続データ・ドメイン規則。データレイヤを叩く・保存する・通信するなど。

| 状態の性質 | 置く場所 |
|------------|----------|
| 1 つの Composable しか使わない UI 要素状態（開閉トグルなど） | その Composable 内部 (`rememberSaveable`) |
| 複数 Composable が読み書きする UI 要素状態 | LCA Composable に `remember*` を置いてダウン |
| UI ロジックが複数フィールド / 複雑 | プレーンな状態ホルダークラス (`remember { FooState() }`) |
| ビジネスロジックを呼ぶ必要がある UI 状態 | ViewModel |
| Screen UI state（ドメインデータ） | ViewModel |

### 3. LCA はどこ？

state を**読み書きする全 Composable**の祖先で、最も深い（=最も内側の）共通ノードを選ぶ。これより上に置くと "property drilling" が深くなり可視性が落ちる。これより下に置くと書き手/読み手のどちらかから到達できない。

### 4. ViewModel か、それとも Composition 内か

ViewModel に置くべきなのは:

- データレイヤ / リポジトリ / UseCase を呼ぶ必要がある
- Screen UI state を集約して `StateFlow` で配信する
- プロセス再生成（システム kill）越しに保持したい（`SavedStateHandle` と組み合わせ）

ViewModel に置くべきでないのは:

- 単なる UI 要素状態（展開、フォーカス、スクロール位置）で、データレイヤを叩く必要がない
- Composition スコープ専用の suspending API（`DrawerState.close()`, `LazyListState.animateScrollToItem()` など）を `viewModelScope` から呼ぼうとしている（`MonotonicFrameClock` が無く `IllegalStateException` で落ちる）

ViewModel を持つ画面でも、すべての state を ViewModel に集める必要はない。**画面内に閉じた UI 要素状態は Composition に置いてよい**。

### 5. ViewModel をどう公開する？

KB が明示している:

- **`ViewModel` インスタンスを下位 Composable に渡さない**（"You shouldn't pass `ViewModel` instances down to other composables"）。screen-level stateful Composable で受け取り、stateless 子に **値 + イベントラムダ**だけ渡す。
- **Wrapper UiState クラスでラムダをまとめて隠さない**。冗長でも個別パラメータで渡すほうが責務が見える（"Property drilling is preferable over creating wrapper classes"）。
- 既定値 `viewModel: FooViewModel = hiltViewModel()` を持つ stateful Composable と、**state と lambda だけを受け取る stateless オーバーロード**の 2 段構成は KB が推奨する典型形。

## 監査ワークフロー

### Step 1: 対象を確定する

- ユーザーが指定した画面・ファイルがあればそれ
- 指定が無ければ `app/src/main/java/.../ui/**/*.kt` のうち `@Composable` を含む新しめのファイルから始める

### Step 2: state を列挙する

各 `@Composable` 関数について以下を抽出する:

- `remember { ... }` / `rememberSaveable { ... }` / `remember*State()` / `mutableStateOf` で作っている state
- ViewModel 由来の state（`collectAsStateWithLifecycle` / `viewModel.foo`）
- Composable 引数として受け取っている state

### Step 3: reader / writer をマッピングする

各 state について、その関数内・呼び出される子関数内のどこで読まれ・書かれるかを `Grep` で洗い出す。子 Composable が状態を変える場合、その変更経路（onXxx ラムダ）を辿る。

### Step 4: 現在の位置 vs 理想の LCA を比較する

差分があるなら、それぞれ片付けるべき "症状" として記録する。代表的な症状:

| 症状 | 直し方 |
|------|--------|
| 子 Composable 内に `rememberSaveable` があるが、親も値を読みたい | 親に上げて値+ラムダで渡す |
| 親で remember しているが、その値を子しか使わない | 子の内部に降ろす |
| ViewModel に `var expanded by mutableStateOf(false)` があるが、ビジネスロジックも他画面利用も無い | Composable 内 `rememberSaveable` に戻す |
| Screen-level Composable が ViewModel をそのまま子に渡している | stateful / stateless に分割し、stateless 側に値+ラムダで渡す |
| Wrapper data class で複数 lambda を束ねている | 個別パラメータに展開する（property drilling 可） |
| ViewModel から `DrawerState.close()` を `viewModelScope.launch` で呼んでいる | Composition の `rememberCoroutineScope()` を渡す形に直す |
| Composable に `Context` / `ContentResolver` を引き回している | ビジネスロジックなら ViewModel + Hilt に移す |

### Step 5: 安全に動かす

修正の順番:

1. **stateless 化を先に**: stateful Composable から、純粋に値+ラムダだけを受け取る stateless オーバーロードを切り出す（プレビュー / テストの足場ができる）。
2. **その後で state を動かす**: LCA に向けて 1 個ずつ移動する。複数同時に動かすと差分が読みづらい。
3. **import と本体は同じ Edit で**: spotless pre-commit が未使用 import を消すため、追加だけのコミットを作らない（CLAUDE.md の規約に従う）。
4. **コミット粒度**: 「stateless 化」「state を ViewModel に上げる」「不要 hoisting を降ろす」など論理単位ごとに分けてコミット。

### Step 6: 検証

毎回回すコマンド（CLAUDE.md より）:

```bash
./gradlew spotlessApply
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew detekt
```

UI 変更が伴うなら、本プロジェクトでは `androidTest/` に `ImagesScreenTest` / `VideosScreenTest` / `PermissionScreenTest` がある。stateless Composable を作ったらそこを起点にテストできるか確認する。

## 出力テンプレート（監査結果を伝えるとき）

ユーザーへ報告するときは以下の形にする:

```markdown
## 監査対象
（ファイル / 画面）

## 参照した KB
- kb://android/develop/ui/compose/state-hoisting
- kb://android/develop/ui/compose/architecture
- kb://...

## 検出した症状
1. <ファイル:行> <状態名> — <症状> → <提案する移動先>
2. ...

## 適用した修正
- <ファイル>: stateful / stateless に分割
- <ファイル>: `expanded` を ViewModel から Composable 内 `rememberSaveable` に降ろした
- ...

## 検証結果
- spotlessApply / assembleDebug / testDebugUnitTest / detekt の結果
```

「検出したが直さない」項目は、なぜ直さないか（範囲外 / KB の例外 / 追加調査が必要）を 1 行添える。

## やらないこと

- **アーキテクチャの一斉刷新**: スコープを「ホイスティング位置」に限定する。Repository を増やす・新規 UseCase を切るなどは [android-implementation-planner](../android-implementation-planner/SKILL.md) の仕事。
- **新規機能の追加**: state の移動だけが対象。挙動が変わる修正は別 PR にする。
- **`Context` ベースの旧 API 移植**: 必要なら別途 [android-kb](../android-kb/SKILL.md) で現行推奨を調べる。

## 例

**質問**: 「ImagesScreen の `permissionsGranted` って ViewModel に上げるべき？」

**やること**:
1. `android docs fetch "kb://android/develop/ui/compose/state-hoisting"` で「ビジネスロジックが絡むなら ViewModel」を再確認
2. `permissionsGranted` の writer (`rememberLauncherForActivityResult` のコールバック) と reader (`if (permissionsGranted) { ... }`) を Grep
3. データレイヤを叩く判定ロジックがあるか確認。無いなら Composition 内、あるなら ViewModel
4. 結論を出力テンプレートで返す。修正が必要なら適用してから `./gradlew assembleDebug testDebugUnitTest spotlessApply detekt` を回す。
