# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# ビルド
./gradlew assembleDebug

# 単体テスト（JVM）
./gradlew testDebugUnitTest

# 特定クラスの単体テスト
./gradlew testDebugUnitTest --tests "io.github.casl0.mediastoreexplorer.ui.images.ImagesViewModelTest"

# インストルメンテッドテスト（実機/エミュレーター必須）
./gradlew connectedDebugAndroidTest

# 単体テストのみのカバレッジ（デバイス不要）
./gradlew jacocoUnitTestCoverageReport
# XML → app/build/reports/jacoco/jacocoUnitTestCoverageReport/jacocoUnitTestCoverageReport.xml

# 単体テスト + インストルメンテッドテストの合算カバレッジ（実機/エミュレーター必須）
./gradlew jacocoDebugCoverageReport
# XML → app/build/reports/jacoco/jacocoDebugCoverageReport/jacocoDebugCoverageReport.xml

# フォーマット自動修正
./gradlew spotlessApply

# フォーマットチェック（CI 用）
./gradlew spotlessCheck

# 静的解析
./gradlew detekt
```

## Architecture

詳細は [docs/architecture.md](docs/architecture.md) を参照。

## Test Structure

### 単体テスト (`src/test/`)

- `MainDispatcherRule` — `@get:Rule` で使用。`Dispatchers.Main` を `UnconfinedTestDispatcher` に差し替え、`viewModelScope` を同期実行させる
- `FakeMediaRepository` — `shouldThrow` で例外注入、`images`/`videos`/`audios`/`downloads` で戻り値制御
- ViewModel テストは `FakeMediaRepository` + `MainDispatcherRule` の組み合わせ

### インストルメンテッドテスト (`src/androidTest/`)

- `FakeMediaRepository` — test/ と同一の実装が androidTest/ にも存在する（別 source set なので重複が必要）
- `MediaRepositoryImpl` を直接インスタンス化する場合は 5 つの DataSource クラス（`ImageMediaDataSource` / `VideoMediaDataSource` / `AudioMediaDataSource` / `DownloadMediaDataSource` / `FileMediaDataSource`）と `ioDispatcher` を渡すこと
- **DataSource を追加・削除したら `androidTest/` の `MediaRepositoryImplTest` も必ず更新する**（コンストラクターが変わるため）
- `MediaRepositoryImplTest` — 実機 ContentResolver を使う統合テスト。`GrantPermissionRule` で権限を自動付与。**このテストが権限を付与したままにするため、Screen テストより後に実行するとテストが失敗する**
- `ImagesScreenTest` / `VideosScreenTest` — 権限未付与状態を `initialPermissionsGranted = false` で注入してテスト（`pm revoke` は実行プロセスをクラッシュさせるため使用不可）

### テスト命名規則

- スペース・コロン不可（テストランナーが失敗する）
- アンダースコア区切り: `functionName_条件_期待結果()`
- セクションは `// region xxx` / `// endregion` でグルーピング

## Coverage (JaCoCo)

Gradle 組み込みの `jacoco` プラグインを使用。

- `jacocoUnitTestCoverageReport` — 単体テストのみ（デバイス不要）
- `jacocoDebugCoverageReport` — 単体テスト + instrumented テストの合算（実機/エミュレーター必須）
- Codecov には `jacocoDebugCoverageReport` で生成された XML を渡す
- `enableUnitTestCoverage = true` / `enableAndroidTestCoverage = true` を debug buildType で有効化済み
- Hilt/Dagger・Compose 生成クラスは `jacocoExcludes` リストで除外済み（`app/build.gradle.kts`）

## Development Workflow

- **コミットは論理的な単位でステップバイステップに行う。** 複数の変更を一度にまとめてコミットしない
- **テストがパスしていることを確認してからコミットする。** ビルドやテストが失敗している状態でコミットしない
- **カバレッジなど計測・検証タスクが正しく動作していることを確認してからコミットする**

## Security

セキュリティ面では **OWASP モバイルアプリケーションセキュリティテストガイド（MASTG）** に従う。
詳細は [docs/security.md](docs/security.md) を参照。

## Coding Guidelines

- **コメントは自明でない箇所にのみ記述する。** 処理の意図や「なぜ」を説明しないコメントは書かない（例：`// リストを返す` のような自明な説明は不要）
- **KDoc は自分が変更したコードにのみ追加する。** 変更していない既存コードに KDoc を後付けしない。追加する場合はパラメーターの制約・副作用など自明でない情報のみ記載する
- **Android ベストプラクティスに従う。**
  - UI ロジックは ViewModel に、View は状態の表示のみに徹する（Unidirectional Data Flow）
  - IO 処理は必ず `@IoDispatcher` で `withContext` を使って切り替える
  - `Context` を ViewModel に持ち込まない
  - Compose では `remember` / `derivedStateOf` を適切に使い不要な再コンポーズを避ける
  - パーミッション要求は `rememberLauncherForActivityResult` で行う

## Key Conventions

- **spotless フックが pre-commit で動作し、未使用 import を自動削除する。** import と使用コードは必ず同じ Edit で追加すること（先に import だけ追加すると除去される）
- 文字列リソースは `values/strings.xml`（英語デフォルト）と `values-ja/strings.xml`（日本語）の2言語
- AGP 9.x のため `compileSdk = release(36) { minorApiLevel = 1 }` という特殊な記法を使用
- `org.jetbrains.kotlin.android` プラグインは削除済み。`kotlinOptions` の代わりに `kotlin { compilerOptions { jvmTarget = JVM_11 } }` を使う
