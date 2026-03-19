# MediaStoreExplorer

[MediaStore](https://developer.android.com/reference/android/provider/MediaStore) のコレクション（画像・動画・音声・ダウンロード・ファイル）をタブで閲覧する Android アプリです。

## 動作要件

- Android 7.0（API 24）以降
- Android Studio Meerkat 以降

## 機能

- 5 つのタブで MediaStore の全コレクションを閲覧：**Images / Videos / Audios / Downloads / Files**
- 権限未付与時は専用の権限リクエスト画面を表示
- 読み取り専用（MediaStore への書き込みなし）

## 技術スタック

| カテゴリ | ライブラリ |
|---|---|
| UI | Jetpack Compose |
| アーキテクチャ | ViewModel + StateFlow（UDF） |
| DI | Hilt |
| 非同期処理 | Kotlin Coroutines |
| ビルド | Gradle（Kotlin DSL） |

## アーキテクチャ

[Android アーキテクチャガイド](https://developer.android.com/topic/architecture) に従った MVVM + Repository + DataSource パターンを採用しています。

```
Screen（Composable）
  └─ ViewModel（StateFlow）
       └─ MediaRepository
            └─ *MediaDataSource（ContentResolver.query）
                 └─ MediaStore
```

詳細は [docs/architecture.md](docs/architecture.md) を参照してください。

## ビルド

```bash
# デバッグビルド
./gradlew assembleDebug

# 単体テスト
./gradlew testDebugUnitTest

# インストルメンテッドテスト（実機/エミュレーター必須）
./gradlew connectedDebugAndroidTest

# コードフォーマット
./gradlew spotlessApply

# 静的解析
./gradlew detekt
```

## カバレッジ

```bash
# 単体テストのみ（デバイス不要）
./gradlew jacocoUnitTestCoverageReport

# 単体テスト + インストルメンテッドテストの合算（実機/エミュレーター必須）
./gradlew jacocoDebugCoverageReport
```

## セキュリティ

[OWASP MASTG](https://mas.owasp.org/MASTG/) に従っています。詳細は [docs/security.md](docs/security.md) を参照してください。

## ライセンス

[Apache License 2.0](LICENSE)
