# Release / Publish ガイド

`main` への push から Google Play 公開までを自動化する CI 設定の概要と、運用に必要な前提・手順を記録する。

## 全体フロー

すべて `release-please.yml` 1 つの workflow 内で完結する。3 つのジョブが
`needs:` で直列に並び、レースや待機ロジックを持たない。

```
main へ push
  ↓
[release-please.yml] release-please ジョブ
  ├─ Conventional Commits を読んでリリース PR を作成/更新
  └─ リリース PR がマージされたら semver タグ + GitHub Release を作成
  ↓ (release_created == 'true' のときのみ)
[release-please.yml] commit-changelog ジョブ
  ├─ fastlane/metadata/android/ja-JP/changelogs/{versionCode}.txt を
  │  semver bump 種別から生成して main に直接 commit & push
  └─ 自身の HEAD SHA を outputs.sha として publish ジョブに渡す
  ↓
[release-please.yml] publish ジョブ
  ├─ commit-changelog.outputs.sha を checkout (changelog を含む状態)
  ├─ AAB を build & 署名
  └─ fastlane deploy で Play Store production トラックに draft として
     アップロード (Play Console で人手承認 → 公開)
```

## 必要な GitHub Secrets

| Secret 名 | 内容 | 取得方法 |
|---|---|---|
| `APP_ID` | release-please が main に push するための GitHub App ID | (既存) |
| `APP_PRIVATE_KEY` | 同 App の private key | (既存) |
| `ANDROID_KEYSTORE_BASE64` | リリース署名 `.jks` を base64 化したもの | `base64 -w0 release.jks` |
| `ANDROID_KEYSTORE_PASSWORD` | キーストアパスワード | ローカルの `keystore.properties` |
| `ANDROID_KEY_ALIAS` | キーエイリアス | 同上 |
| `ANDROID_KEY_PASSWORD` | キーパスワード | 同上 |
| `PLAY_SERVICE_ACCOUNT_JSON` | Play Console 用 service account の JSON 全文 | GCP コンソールで生成し Play Console で権限付与 |

`PLAY_SERVICE_ACCOUNT_JSON` の service account には Play Console で「リリース管理者」相当の権限を付与する。

## 前提条件

- **初回は手動アップロード必須**: Google Play Console は初回アップロードを API 経由で受け付けない。初回 AAB は Play Console から手動でアップロードしておくこと。CI が動くのは 2 回目以降のリリースから
- release-please が GitHub App token (`APP_ID` / `APP_PRIVATE_KEY`) を使っていること
- `Gemfile` / `Gemfile.lock` がコミット済みであること (fastlane のバージョン固定)
- `app/build.gradle.kts` の release signingConfig が `KEYSTORE_PATH` 等の環境変数フォールバックに対応していること

## 通常のリリース手順

1. `main` に `feat: ...` / `fix: ...` の Conventional Commit を積む
2. release-please が「Release v1.x.x」PR を作成/更新する。CHANGELOG.md / `app/build.gradle.kts` のバージョンも自動更新される
3. リリース PR をマージする
4. `release-please.yml` が自動で:
   - semver タグと GitHub Release を作成
   - Play Store changelog ファイル (`fastlane/metadata/android/ja-JP/changelogs/{versionCode}.txt`) を main に直接 commit
   - 続けて publish ジョブで AAB を Play Console の production / draft へアップロード
5. **Play Console で内容を確認し、手動で「公開」ボタンを押す** (`release_status: "draft"` のため自動公開はされない)

## 再実行

Play 側エラー等で publish のみ再実行したい場合は、`release-please.yml` の該当 run を GitHub Actions UI から **Re-run failed jobs** で再実行する。release-please / commit-changelog は成功状態を引き継ぎ、publish のみが再度走る。

「Re-run all jobs」は release-please が同じ commit に対しては release を再作成しない (`release_created == 'false'` になる) ため後続ジョブもスキップされる。Re-run failed jobs を使うこと。

## トラブルシューティング

### `commit-changelog` ジョブが changelog を変更なしと判定する

すでに同じ versionCode の changelog がコミット済みのケース。ジョブはスキップせず HEAD SHA を outputs に出して publish に進むため、通常は無害。意図せず内容が古いまま deploy された場合は、`fastlane/metadata/android/ja-JP/changelogs/{versionCode}.txt` を手動で修正してから Re-run failed jobs で publish を再実行する。

### `upload_to_play_store` が 403 / 認証エラー

`PLAY_SERVICE_ACCOUNT_JSON` の service account に Play Console 側で十分な権限が付いていない可能性が高い。Play Console → 設定 → API アクセス で当該 service account の権限 (リリース管理者) を再確認する。

### `upload_to_play_store` が「app not found」エラー

初回手動アップロードが完了していない、または `fastlane/Appfile` の `package_name` が Play Console 上のものと一致していない。

## changelog のフォーマット要件

- ファイル名: versionCode (= `major*10000 + minor*100 + patch`) `.txt`
- 内容: 個々のコミットを書き出さず、release-please の semver bump 種別から固定文を 1 行出力する:
  - `patch` が増えたリリース → `不具合修正と細かな改善を行いました。`
  - それ以外 (`feat` / BREAKING CHANGE を含むリリース) → `新機能の追加と改善を行いました。`
- ja-JP のみ自動化。他ロケールが必要になったら `release-please.yml` の `commit-changelog` ジョブで複数ロケールへ書き出すよう拡張する
