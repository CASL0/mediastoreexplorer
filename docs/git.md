# Git Rules

## ブランチ戦略（GitHub Flow）

- `main` ブランチは常にリリース可能な状態を保つ
- 作業は必ず `main` から新しいブランチを切って行う
- ブランチ名は `<type>/<description>` の形式にする（例: `feat/file-list-screen`, `fix/sync-status-bug`）
- 作業完了後は Pull Request を作成して `main` にマージする
- マージ後はブランチを削除する

## コミットの粒度

- **意味のある論理単位ごとにコミットする。** 無関係な変更を1つのコミットに混ぜない
- **各コミットはビルドとテストが通る状態にする。** `git bisect` で問題のコミットを特定しやすくするため、途中の壊れた状態をコミットしない

## コミットメッセージ（Conventional Commits）

```
<type>[optional scope]: <description>

[optional body]

[optional footer]
```

### type一覧

| type       | 用途                                           | semver |
| ---------- | ---------------------------------------------- | ------ |
| `feat`     | 新機能                                         | minor  |
| `fix`      | バグ修正                                       | patch  |
| `docs`     | ドキュメントのみの変更                         | -      |
| `style`    | コードの意味に影響しない変更（フォーマット等） | -      |
| `refactor` | バグ修正でも機能追加でもないコード変更         | -      |
| `test`     | テストの追加・修正                             | -      |
| `chore`    | ビルドプロセスや補助ツールの変更               | -      |
| `perf`     | パフォーマンス改善                             | patch  |
| `ci`       | CI設定の変更                                   | -      |

### BREAKING CHANGE

破壊的変更は footer に `BREAKING CHANGE: <description>` を記載するか、type の後に `!` を付ける。

```
feat!: ストレージAPIを刷新

BREAKING CHANGE: StorageService のインターフェースが変更されました
```

### コミット例

```
feat(images): 画像一覧画面を追加
fix(deps): update dependency androidx.compose:compose-bom to v2026
chore: spotless フックを追加
ci: lint 用のジョブを追加
```

## semantic-release

`main` へのマージ時に semantic-release が自動でバージョンを決定してリリースする。

| コミットに含まれる type | バージョン変化 |
| ----------------------- | -------------- |
| `BREAKING CHANGE`       | major (x.0.0)  |
| `feat`                  | minor (0.x.0)  |
| `fix`, `perf`           | patch (0.0.x)  |
| それ以外                | リリースなし   |
