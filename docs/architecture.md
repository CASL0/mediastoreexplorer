# Architecture

本プロジェクトのアーキテクチャは以下の Android Developer 公式ガイドに従う。

- [アプリ アーキテクチャ ガイド](https://developer.android.com/topic/architecture)
- [Android アーキテクチャに関する推奨事項](https://developer.android.com/topic/architecture/recommendations)

---

## 基本原則

- **関心の分離（Separation of Concerns）** — UI・ドメイン・データの各層を明確に分離する
- **単一信頼情報源（Single Source of Truth）** — データ型ごとに1つのオーナーを持ち、他はイベント経由で変更する
- **単方向データフロー（Unidirectional Data Flow）** — 状態は上位から下位へ流れ、イベントは逆方向に流れる
- **データモデルで UI を駆動する** — UI コンポーネントのライフサイクルに依存しない永続的なデータモデルを使用する

---

## レイヤー構成

```text
┌──────────────────────────────┐
│          UI Layer            │
│  Screen（Composable）        │
│  ViewModel（状態ホルダー）    │
└──────────────┬───────────────┘
               │
               │  ※ドメイン層は複雑なビジネスロジックが
               │    複数のViewModelで共有される場合に導入する
               │    （現時点では省略）
               │
┌──────────────▼───────────────┐
│         Data Layer           │
│  Repository（インターフェース）│
│  RepositoryImpl（実装）       │
└──────────────────────────────┘
```

### UI 層

| 要素 | 実装 | 責務 |
| --- | --- | --- |
| UI Elements | `ImagesScreen.kt` / `VideosScreen.kt` / `AudiosScreen.kt` / `DownloadsScreen.kt` | 状態の表示とユーザーイベントの受け付け |
| State Holder | `ImagesViewModel.kt` / `VideosViewModel.kt` / `AudiosViewModel.kt` / `DownloadsViewModel.kt` | UI 状態を `StateFlow` で保持・公開、ビジネスロジックの実行 |
| 共通 UI | `MediaTable.kt` / `PermissionScreen.kt` | 再利用可能な Composable |
| Preview 用 Repository | `PreviewMediaRepository.kt` | `@Preview` 専用のノーオペレーション実装。R8 がリリースビルドで除去する |

**UI 状態の公開パターン（推奨に従う）：**

```kotlin
// StateFlow + WhileSubscribed(5_000) で効率的に公開
val uiState: StateFlow<ImagesUiState> = ...
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ImagesUiState()
    )
```

**UI での収集（ライフサイクル対応）：**

```kotlin
// Compose では collectAsStateWithLifecycle() を使用
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

### データ層

データ層は by-type 構成（`model/` / `repository/` / `datasource/` を data 直下にフラット配置）。

| 要素 | 実装 | 責務 |
| --- | --- | --- |
| Repository インターフェース | `repository/MediaRepository.kt` / `repository/UserPreferencesRepository.kt` | UI に対する契約定義 |
| Repository 実装 | `repository/MediaRepositoryImpl.kt` / `repository/UserPreferencesRepositoryImpl.kt` | `withContext(@IoDispatcher)` でスレッド切替し DataSource / DataStore に委譲 |
| DataSource | `datasource/ImageMediaDataSource.kt` など (5クラス) | コレクションごとに `ContentResolver.query()` → Cursor マッピング |
| Cursor 拡張関数 | `datasource/CursorExtensions.kt` | `opt*Col` / `opt*ColQ` / `opt*ColR` / `opt*ColU` — nullable 列読み取りと API バージョンガードを集約 |
| モデル | `model/ImageItem` / `VideoItem` / `AudioItem` / `DownloadItem` / `FileItem` / `UserPreferences` / `ThemeMode` | データ層公開データクラスと enum を集約 |

---

## パッケージ構成

```text
app/src/main/java/.../
├── MainActivity.kt              # @AndroidEntryPoint、HorizontalPager + TabRow で4タブ管理
├── MediaStoreExplorerApp.kt     # @HiltAndroidApp
├── di/
│   ├── AppModule.kt             # ContentResolver, @IoDispatcher の Singleton 提供
│   ├── RepositoryModule.kt      # MediaRepository → MediaRepositoryImpl のバインド
│   └── DispatcherQualifiers.kt  # @IoDispatcher アノテーション定義
├── data/
│   ├── model/                   # ImageItem / VideoItem / AudioItem / DownloadItem / FileItem
│   │                            # UserPreferences / ThemeMode
│   ├── repository/
│   │   ├── MediaRepository.kt + Impl
│   │   └── UserPreferencesRepository.kt + Impl
│   └── datasource/
│       ├── CursorExtensions.kt  # Cursor opt*Col 拡張関数
│       ├── ImageMediaDataSource.kt
│       ├── VideoMediaDataSource.kt
│       ├── AudioMediaDataSource.kt
│       ├── DownloadMediaDataSource.kt
│       └── FileMediaDataSource.kt
└── ui/
    ├── common/
    │   ├── MediaTable.kt
    │   ├── PermissionScreen.kt
    │   └── FormatUtils.kt
    ├── images/
    │   ├── ImagesViewModel.kt
    │   └── ImagesScreen.kt
    ├── videos/                  # images と対称な構造
    ├── audios/                  # images / videos と対称な構造
    ├── downloads/               # images / videos / audios と対称な構造
    └── preview/
        └── PreviewMediaRepository.kt  # @Preview 専用（R8 がリリースビルドで除去）
```

---

## データフロー

```text
Screen
 │  起動時に checkSelfPermission
 ├─ 権限なし ──→ PermissionRequiredScreen
 └─ 権限あり ──→ LaunchedEffect → viewModel.load()
                    │
                    ▼
               ViewModel（viewModelScope.launch）
                    │
                    ▼
               Repository.get*()
                    │  withContext(@IoDispatcher)
                    ▼
               DataSource.get*()
                    │
                    ▼
               ContentResolver.query()  ─→  MediaStore
                    │
                    ▼
               StateFlow<UiState> 更新
                    │
                    ▼
               Screen 再コンポーズ
```

---

## 依存性注入

[Android 推奨事項](https://developer.android.com/topic/architecture/recommendations#dependencies) に従い Hilt を使用する。

- コンストラクタインジェクションを優先する
- Repository は `@Singleton` スコープで提供する
- IO 処理用の `CoroutineDispatcher` は `@IoDispatcher` で識別する

```kotlin
@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel()
```
