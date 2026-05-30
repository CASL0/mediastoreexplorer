# Compose ガイドライン

本プロジェクトの Jetpack Compose API は、Compose 公式の API 設計ガイドラインに従う。

- [Compose API Guidelines（androidx 公式）](https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md)
- [Compose API ガイドライン（Android Developers / 日本語）](https://developer.android.com/develop/ui/compose/api-guidelines)

公式ガイドラインは要件レベルを **Framework / Library / App** の 3 つの対象別に定めている。本プロジェクトは **アプリ開発（App development）** に該当するため、Framework / Library で `MUST` とされる項目の多くは `SHOULD` 相当として適用する。ただし可読性と一貫性のため、本プロジェクトでは以下を原則 **必須** とする。

本ドキュメントは Compose の Composable 設計に関する指針を網羅する — 命名・引数・Modifier・スロット API といった **API 設計**に加え、**UDF（単方向データフロー）** と **状態ホイスティング**（Stateless / Controlled、State と Event の分離、Hoisted state type、インターフェース化）も含む。

なお、**層をまたぐアーキテクチャ全体方針**（UI / Domain / Data の分離、Single Source of Truth とデータ層の関係）は [architecture.md](architecture.md) を、**個別の状態をどこに置くかの判断**（ViewModel に上げるか Composable 内に留めるか）は `compose-state-hoisting` スキルを併せて参照する。

---

## 命名規則

### Unit を返す @Composable は PascalCase の名詞

UI ツリーを emit する（`Unit` を返す）Composable は、クラスと同じく **PascalCase の名詞**（必要なら形容詞付き）で命名する。動詞・動詞句は使わない。

```kotlin
// Do
@Composable fun MediaTable(...) { ... }
@Composable fun PermissionGate(...) { ... }
@Composable fun PermissionRequiredScreen(...) { ... }

// Don't
@Composable fun renderTable(...) { ... }   // 動詞
@Composable fun showPermission(...) { ... } // 動詞
```

宣言的エンティティであり、composition 上の「存在／非存在」がそのまま UI に反映されるという心的モデルを命名で補強する。

### 値を返す @Composable は camelCase

UI を emit せず値を返す `@Composable` 関数は、通常の Kotlin 関数と同じ **camelCase** で命名する。型のファクトリのように PascalCase にしない。

```kotlin
// Do — 値（List<TableColumn>）を返すので camelCase
@Composable
private fun imageMediaColumns(): List<TableColumn<ImageItem>> { ... }

@Composable
private fun imagesRequiredPermissions(): List<String> { ... }
```

参考: [imageMediaColumns()](../app/src/main/java/io/github/casl0/mediastoreexplorer/ui/images/ImagesScreen.kt#L107)。

### オブジェクトを記憶する関数は remember プレフィックス

内部で `remember` してオブジェクトをキャッシュ・保持する関数は `remember` を接頭辞に付ける（例: `rememberCoroutineScope()`、`rememberScrollState()`）。呼び出し側に「recomposition をまたいで保持される副作用がある／呼び出し側で重ねて `remember {}` する必要はない」ことを伝えるため。

### 定数・enum は PascalCase

`CAPITALS_AND_UNDERSCORES` ではなく **PascalCase** を使う。`val` / companion object / enum entry の命名を統一し、実装詳細に依存しない一貫した期待値を与えるため。

```kotlin
// Do
enum class ThemeMode { System, Light, Dark }
```

---

## 引数の順序

Composable の引数は次の順序で並べる。

1. **必須パラメータ**（デフォルト値なし）
2. **`modifier: Modifier = Modifier`** — 最初のオプション引数として置く
3. その他のオプションパラメータ（デフォルト値あり）
4. **末尾の `content: @Composable () -> Unit`**（スロット）

```kotlin
@Composable
fun PermissionGate(
    permissions: List<String>,          // 必須
    message: String,                    // 必須
    rationaleMessage: String,           // 必須
    onGranted: () -> Unit,              // 必須
    modifier: Modifier = Modifier,      // 最初のオプション = modifier
    initialGrantedOverride: Boolean? = null, // その他オプション
    content: @Composable () -> Unit,    // 末尾スロット
)
```

参考: [PermissionGate()](../app/src/main/java/io/github/casl0/mediastoreexplorer/ui/common/PermissionGate.kt#L35)。末尾に `content` を置くことで trailing lambda 構文で呼べる。

---

## Modifier 規約

UI を 1 ノード emit する Element 系 Composable は、以下を守る。

- `Modifier` 型の引数を **`modifier`** という名前で受け取る
- `modifier` は **最初のオプション引数**（デフォルト `Modifier`）
- 受け取った `modifier` を **root の UI ノードに渡す**
- 追加の modifier は受け取ったチェーンの **末尾に append** する。**先頭に prepend しない**

```kotlin
@Composable
fun <T> MediaTable(
    items: List<T>,
    columns: List<TableColumn<T>>,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier,   // 最初のオプション引数
    key: (T) -> Any,
) {
    when {
        isLoading ->
            Box(modifier.fillMaxSize(), ...) { ... } // 受け取った modifier を root に渡す
        ...
    }
}
```

参考: [MediaTable()](../app/src/main/java/io/github/casl0/mediastoreexplorer/ui/common/MediaTable.kt#L55)。modifier を要求することで、呼び出し側が余計なラップ用レイアウトを挟まずに装飾・配置できる。

---

## 単方向データフロー（UDF）

Compose は **状態（state）を入力として受け取り、UI を出力する**。公式ガイドラインの言葉では「**Compose はイベントではなく状態を入力として扱う。Composable 関数は状態のオブザーバーである**（Compose operates on _state_ as input, not _events_. Composable functions are _state observers_）」。

このため UI は次の単方向ループで駆動する。

```text
状態（state）── 下へ流れる ──▶ Composable（描画）
   ▲                                  │
   └──── イベント（event）が上へ流れる ◀┘
        onXxx コールバックで通知
```

- **状態は上から下へ流れる** — ViewModel が所有する `StateFlow<UiState>` を Composable が読み取って描画する
- **イベントは下から上へ流れる** — Composable は状態を直接書き換えず、`onXxx: () -> Unit` コールバックで上位（最終的に ViewModel）に通知する
- Composable は実行中に読んだパラメータと `mutableStateOf()` の値を「入力」として扱う。同じ入力なら同じ結果になる（**べき等**）よう設計する

UDF の層をまたぐ全体方針（Single Source of Truth、データ層との関係）は [architecture.md](architecture.md) を参照。本セクションは Composable レベルの状態設計に焦点を当てる。

---

## 状態ホイスティング

### Stateless / Controlled Composable

公式ガイドラインは、Composable を **内部状態を持たず、呼び出し側が所有する状態を引数で受け取る「controlled」** な形で設計することを **SHOULD**（Framework / Library。App は推奨）としている。

```kotlin
// 公式の例 — 状態（isChecked）と変更イベント（onToggle）を別々に受け取る
@Composable
fun Checkbox(isChecked: Boolean, onToggle: () -> Unit)

// 呼び出し側が状態を所有・変更する
Checkbox(myState.optIn, onToggle = { myState.optIn = !myState.optIn })
```

- `initialValue` を受け取って内部 `remember { mutableStateOf(...) }` で状態を抱え込む設計は **避ける**。呼び出し側がバリデーションポリシーを実装したり Single Source of Truth を保てなくなるため
- 状態（値）と、それを変更するイベントコールバックを **別々の引数**で受け取る

### State と Event を分離する

公式ガイドラインは「観測可能なイベント」と「観測可能な状態」を明確に区別する。

| | 観測可能なイベント（Observable event） | 観測可能な状態（Observable state） |
| --- | --- | --- |
| 性質 | ある時点で発生し、通知後は破棄される | 値。不等な値へ遷移したときだけ変更通知を出す |
| 重複 | 等しいイベントの繰り返しにも意味がある | conflate される（**最新の値だけ**が意味を持つ） |
| オブザーバー | すべてのイベントを受け取る必要がある | **べき等**でなければならない（同じ状態なら同じ結果） |

**Compose が入力として扱うのは「状態」であって「イベント」ではない。** イベント的な制御が必要な場合は、状態をホイストしたコールバックやステートホルダー経由で表現する（[emit と return を混在させない](#emit-と-return-を混在させない) も参照）。

### ViewModel 版 + stateless overload の 2 段構成（プロジェクト固有）

本プロジェクトの Screen は、上記 controlled 設計を具体化するため、**ViewModel 注入版**と **stateless overload** の 2 つを同名でオーバーロードする。Preview / Test では stateless 版を直接呼ぶ。

```kotlin
// ViewModel 版 — StateFlow を collect して stateless 版に委譲するだけ
@Composable
fun ImagesScreen(
    viewModel: ImagesViewModel,
    modifier: Modifier = Modifier,
    initialPermissionsGranted: Boolean? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ImagesScreen(
        uiState = uiState,
        onLoadImages = viewModel::loadImages,
        modifier = modifier,
        initialPermissionsGranted = initialPermissionsGranted,
    )
}

// stateless overload — 状態とイベントだけを受け取る。Preview / Test はこちらを使う
@Composable
fun ImagesScreen(
    uiState: ImagesUiState,
    onLoadImages: () -> Unit,
    modifier: Modifier = Modifier,
    initialPermissionsGranted: Boolean? = null,
) { ... }
```

参考: [ImagesScreen()](../app/src/main/java/io/github/casl0/mediastoreexplorer/ui/images/ImagesScreen.kt#L42)。`Context` を ViewModel に持ち込まないこと、IO は `@IoDispatcher` で切り替えることは [architecture.md](architecture.md) の規約に従う。

### StateFlow の収集

UI 状態は ViewModel が `StateFlow` で公開し、Compose 側ではライフサイクル対応の `collectAsStateWithLifecycle()` で収集する。

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

### Hoisted state type（XxxState 型）

引数が増えて関連する状態とコールバックが煩雑になったら、公式ガイドラインに従い **専用のステートホルダー型**に括り出す。Framework / Library では以下が **SHOULD**。アプリ開発では当面 UiState ＋ stateless overload で足りるが、再利用可能な共通 Composable が複雑な内部状態を持つようになったらこのパターンへ昇格させる。

公式が定める要件は次の通り。

- 命名は **「Composable 名 + `State`」**（例: `VerticalScroller` → `VerticalScrollerState`）
- `@Stable` を付け、`@Stable` 契約を正しく実装する
- 具象 / open class ではなく **インターフェース**として宣言する（隠れた状態同期契約を生まないため）
- 同名の **ファクトリ関数**でデフォルト実装を提供する
- デフォルト引数では `remember {}` を使って簡単なデフォルトを与えつつ、高度なカスタマイズも可能にする
- 内部状態生成のセンチネルとして **`null` を使わない**（後で null に意味を持たせたとき破綻するため）

```kotlin
// 公式の例
@Stable
interface VerticalScrollerState {
    var scrollPosition: Int
    var scrollRange: Int
}

// 型と同名のファクトリ関数でデフォルト実装を提供
fun VerticalScrollerState(): VerticalScrollerState = VerticalScrollerStateImpl()

@Composable
fun VerticalScroller(
    state: VerticalScrollerState = remember { VerticalScrollerState() },
)
```

### インターフェースによる拡張性

ステートホルダーを **具象 final 型**で公開すると、複数のライブラリ／チームがそれぞれ「自分が信頼できる唯一の状態」を主張し、誤りやすい同期が必要になる。公式ガイドラインは、ステートホルダー型を **インターフェース**として宣言し、統合者が複数のインターフェースを 1 つの型で実装して Single Source of Truth を保てるようにすることを **SHOULD**（Framework / Library）としている。

```kotlin
// 異なるシステム由来の状態を 1 つの型で実装し、単一の信頼できる状態を保つ
class MyState(name: String, avatarUrl: String) : FooState, BarState { ... }
```

アプリ開発では、まず **具象型**から始めてよい。抽象化が本当に必要になった段階でインターフェースへ昇格すればよく、同名ファクトリ関数の追加はソース互換なので既存の呼び出しを壊さない。

---

## emit と return を混在させない

`@Composable` 関数は **UI ツリーを emit する** か **値を返す** かのどちらか一方にする。両方は行わない。emit の順序が composition ツリー構造を決めるため、戻り値を持たせると宣言的パターンと両立しない順序制約が生じる。

制御が必要なら、状態をホイストしたインターフェース／コールバックを引数で受け取る。

---

## スロット API（content ラムダ）

子 Composable を引数に取る Layout 系では、スロットの命名と配置を統一する。

- 単一の `@Composable` 引数は **`content`** と命名する
- 複数ある場合も、主要・最頻のスロットを `content` とする
- 主要な `@Composable` 引数は **最後**に置く（trailing lambda 構文を可能にするため）

`PermissionGate` は権限が付与された後に表示する本体を `content: @Composable () -> Unit` として末尾に受け取り、呼び出し側は trailing lambda で本体を渡す。参考: [PermissionGate()](../app/src/main/java/io/github/casl0/mediastoreexplorer/ui/common/PermissionGate.kt#L35)。

---

## Stable / Immutable

UI 状態やデータ型の安定性は recomposition の最適化に直結する。

- 構築後に変化しない型には `@Immutable` を付与してよい
- Snapshot システムが変更を通知する可変型には `@Stable` を付与してよい
- 一度公開した型から `@Stable` / `@Immutable` を**外さない**（契約違反になる）
- カスタム `equals()` を持つ `@Stable` 型では、同じ 2 参照に対する `equals` の結果が常に一致しなければならない

UiState など Compose に渡すデータは、`data class` + 不変プロパティ（`List` などの読み取り専用コレクション）で構成し、不要な再コンポーズを避ける。

---

## 不要な再コンポーズを避ける

- `remember` / `derivedStateOf` を適切に使い、再計算・再生成を抑える
- `LazyColumn` などのリストでは安定した **`key`** を指定する

```kotlin
LazyColumn(modifier = modifier.fillMaxSize()) {
    stickyHeader { ... }
    itemsIndexed(items, key = { _, item -> key(item) }) { index, item -> ... }
}
```

参考: [MediaTable()](../app/src/main/java/io/github/casl0/mediastoreexplorer/ui/common/MediaTable.kt#L85)。`ImagesTable` は `key = { it.id }` のように MediaStore の安定 ID を渡している。

---

## derivedStateOf を使うべきとき

`derivedStateOf` は **入力の state が、UI を更新したい頻度よりも頻繁に変化する**ときに使う。Kotlin Flow の `distinctUntilChanged` に相当し、不要な再コンポーズをフィルタリングするのが役割。

> 判断基準: **入力 state が「出力したい変化」より多く変わる**なら使う。入力と出力が同じ頻度で変わるなら使わない。

参考: [When should I use derivedStateOf?（Android Developers Blog）](https://medium.com/androiddevelopers/jetpack-compose-when-should-i-use-derivedstateof-63ce7954c11b)。

### 使うべき例（入力 ≫ 出力）

スクロール位置のように頻繁に変わる値から、稀にしか変わらない真偽値を導出する場合。

```kotlin
// firstVisibleItemIndex は 0,1,2... と頻繁に変わるが、出力 Boolean は true/false の切替のみ
val showScrollToTop = remember {
    derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }
}
```

入力（インデックスやスクロールオフセット）は毎フレーム変化しうるが、出力（ボタンの表示可否）はめったに変わらない。この**頻度の差**がある場面でこそ `derivedStateOf` が効く。

### 使うべきでない例（入力 ＝ 出力）

複数の state を単に連結・合成するだけで、出力が入力と同じ頻度で変わる場合は使わない。オーバーヘッドが増えるだけで何の効果もない。

```kotlin
// DON'T — fullName は firstName / lastName が変わるたびに必ず変わる（頻度差がない）
val fullName = remember {
    derivedStateOf { "$firstName $lastName" }
}

// DO — そのまま合成すればよい
val fullName = "$firstName $lastName"
```

### state でない変数はキーに渡す

`derivedStateOf` が変化を追跡するのは **Compose の state オブジェクトだけ**。通常の変数（関数引数など）は計算ブロック内で初期値のまま固定され、後から変わっても再計算されない。そうした非 state の値に依存する場合は、`remember` の **key** に渡して再初期化させる。

```kotlin
// WRONG — threshold が変わっても無視される
val isEnabled = remember {
    derivedStateOf { scrollPosition > threshold }
}

// CORRECT — threshold をキーにすると、変化時に derivedStateOf を作り直す
val isEnabled = remember(threshold) {
    derivedStateOf { scrollPosition > threshold }
}
```

### remember(key) との使い分け

| 状況 | 使うもの |
| --- | --- |
| key が変わるたびに UI を更新したい | `remember(key) { ... }` |
| 入力が出力より頻繁に変わる（頻度差がある） | `remember { derivedStateOf { ... } }` |
| 非 state の値にも依存する derived state | `remember(key) { derivedStateOf { ... } }` |

---

## パーミッション要求

ランタイム権限の要求は Composable 内で行い、状態ロジックを共通ゲートに集約する。

- 本プロジェクトでは Accompanist Permissions（`rememberMultiplePermissionsState`）を用いた [PermissionGate](../app/src/main/java/io/github/casl0/mediastoreexplorer/ui/common/PermissionGate.kt) に集約する
- 自前で `rememberLauncherForActivityResult` を使う場合も、ロジックを Screen に散らさずゲート Composable に寄せる
- Preview / Test 用に、OS 状態を経由せず権限状態を強制できるオーバーライド引数（`initialGrantedOverride` / `initialPermissionsGranted`）を用意する。`pm revoke` は実行プロセスをクラッシュさせるため使えない（[CLAUDE.md](../CLAUDE.md) のテスト方針参照）

---

## Preview 規約

- Preview 関数は `private` の `@Composable` とし、`MediaStoreExplorerTheme {}` でラップする
- stateless overload を直接呼び、UiState やコールバック（`{}`）を直接渡す
- ローディング・エラー・空・データありなど **状態ごとに別 Preview** を用意する
- ダークテーマは `@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)` を重ねて確認する
- `@Preview` 専用の Repository 実装は [PreviewMediaRepository](../app/src/main/java/io/github/casl0/mediastoreexplorer/ui/preview/PreviewMediaRepository.kt)（R8 がリリースビルドで除去）を使う

```kotlin
@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun MediaTableWithDataPreview() {
    MediaStoreExplorerTheme {
        MediaTable(
            items = listOf("photo_001.jpg", "photo_002.png", "video_clip.mp4"),
            columns = previewColumns,
            isLoading = false,
            error = null,
            key = { it },
        )
    }
}
```

参考: [MediaTable.kt の Preview 群](../app/src/main/java/io/github/casl0/mediastoreexplorer/ui/common/MediaTable.kt#L162)。

---

## OptIn / Suppress の扱い

- Experimental API（`ExperimentalFoundationApi` / `ExperimentalMaterial3Api` / `ExperimentalPermissionsApi` など）は、使う関数に **`@OptIn` を個別付与**する（集中設定に逃がさない）
- detekt / lint の `@Suppress` は理由コメントを必ず添える。例: `@Suppress("LongParameterList") // … 汎用テーブルのため`
- `@Preview` の除外など横断的な設定のみ集中設定でよい

---

## 対象別の要件サマリ（公式 → 本プロジェクト）

公式の要件レベルは Framework / Library / App の 3 対象別。下表は **App 列に公式のアプリ開発向けレベル**を、本プロジェクト列に運用上の扱いを示す（App に明示規定がなく Framework / Library が SHOULD の項目は「—（Lib: SHOULD）」と併記）。

| ガイドライン | 公式（App） | 本プロジェクト |
| --- | --- | --- |
| Unit Composable は PascalCase 名詞 | SHOULD | 必須 |
| 値返し Composable は camelCase | MUST | 必須 |
| `modifier` 引数を受け取り root へ渡す | SHOULD | 必須（Element 系） |
| `modifier` を最初のオプション引数に置く | SHOULD | 必須 |
| UDF（状態は下へ／イベントは上へ） | SHOULD | 必須 |
| Stateless / Controlled Composable | SHOULD | 必須（Screen） |
| State と Event を分離する | SHOULD | 必須 |
| emit XOR return | SHOULD | 必須 |
| 状態ホイスティング（stateless overload） | — | 必須（Screen） |
| Hoisted state type は `XxxState` / `@Stable` / interface | —（Lib: SHOULD） | 複雑化したら昇格 |
| ステートホルダーをインターフェース化 | —（Lib: SHOULD） | 必要時に昇格（まず具象型） |
| スロットは `content`、末尾配置 | SHOULD | 必須 |
| `@Stable` / `@Immutable` の付与 | —（Lib: SHOULD） | 推奨 |
| 状態ごとの Preview | — | 推奨 |
