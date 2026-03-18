# Security

本プロジェクトのセキュリティ方針は以下の OWASP 公式ドキュメントに従う。

- [OWASP MASVS](https://mas.owasp.org/MASVS/) — Mobile Application Security Verification Standard
- [OWASP MASTG](https://mas.owasp.org/MASTG/) — Mobile Application Security Testing Guide

新機能の実装・コードレビュー時は該当するコントロールを確認すること。

---

## MASVS-STORAGE — データストレージ

> 参照: [MASVS-STORAGE-1](https://mas.owasp.org/MASVS/controls/MASVS-STORAGE-1/) / [MASVS-STORAGE-2](https://mas.owasp.org/MASVS/controls/MASVS-STORAGE-2/)

アプリが意図的に処理するデータのみにアクセスを制限し、機密データを不用意に公開しない。

- 機密データを平文でローカルストレージ（SharedPreferences / Room / ファイル）に書き込まない
- `android:allowBackup="false"` を維持する。バックアップが必要なデータを追加する場合は `backup_rules.xml` / `data_extraction_rules.xml` に明示的なルールを設定する
- 外部ストレージに機密データを書き込まない
- ログ（`Log.*`）に機密情報（ファイルパス・位置情報・個人情報等）を出力しない
- 位置情報など機密性の高い情報を UI に表示する場合はユーザーへの明示的な周知を行う
- スクリーンショットやバックグラウンド時のキャッシュに機密データが残らないようにする（必要に応じて `FLAG_SECURE` を使用する）

---

## MASVS-CRYPTO — 暗号

> 参照: [MASVS-CRYPTO-1](https://mas.owasp.org/MASVS/controls/MASVS-CRYPTO-1/) / [MASVS-CRYPTO-2](https://mas.owasp.org/MASVS/controls/MASVS-CRYPTO-2/)

業界標準の暗号アルゴリズムとプロトコルを使用する。

- 独自の暗号アルゴリズムを実装しない
- 非推奨アルゴリズム（MD5、SHA-1、DES、RC4）を使用しない
- 鍵は Android Keystore System を使用して管理する
- 乱数生成には `SecureRandom` を使用する

---

## MASVS-PLATFORM — プラットフォームとの対話

> 参照: [MASVS-PLATFORM-1](https://mas.owasp.org/MASVS/controls/MASVS-PLATFORM-1/) / [MASVS-PLATFORM-2](https://mas.owasp.org/MASVS/controls/MASVS-PLATFORM-2/) / [MASVS-PLATFORM-3](https://mas.owasp.org/MASVS/controls/MASVS-PLATFORM-3/)

Android プラットフォームの機能を安全に使用し、意図しないデータ漏洩や不正アクセスを防ぐ。

- 必要最小限の権限のみを Manifest に宣言する
- `exported="true"` のコンポーネントを最小化し、intent-filter を限定する
- WebView を使用する場合は `setJavaScriptEnabled(false)` を原則とする
- Deep Link を使用する場合は受け取るデータを必ず検証する
- 他アプリへ渡す Intent には機密データを含めない
- ContentProvider を公開する場合は適切な権限を設定する

---

## MASVS-AUTH — 認証・認可

> 参照: [MASVS-AUTH-1](https://mas.owasp.org/MASVS/controls/MASVS-AUTH-1/) / [MASVS-AUTH-2](https://mas.owasp.org/MASVS/controls/MASVS-AUTH-2/) / [MASVS-AUTH-3](https://mas.owasp.org/MASVS/controls/MASVS-AUTH-3/)

認証・認可を実装する場合はプラットフォームの仕組みを活用し、独自実装を避ける。

- 認証情報（パスワード・トークン・API キー等）をコードにハードコードしない
- 認証トークンは Android Keystore や EncryptedSharedPreferences に保存する
- 生体認証を使用する場合は `BiometricPrompt` API を使用し、フォールバック（PIN/パターン）も適切に実装する
- セッショントークンは十分なエントロピーを持ち、有効期限を設ける
- サーバーサイドで認証・認可を必ず検証する（クライアントのみの制御に依存しない）

---

## MASVS-NETWORK — ネットワーク通信

> 参照: [MASVS-NETWORK-1](https://mas.owasp.org/MASVS/controls/MASVS-NETWORK-1/) / [MASVS-NETWORK-2](https://mas.owasp.org/MASVS/controls/MASVS-NETWORK-2/)

ネットワーク通信を追加する場合は以下を遵守する。

- 平文 HTTP 通信を許可しない（`network_security_config.xml` で `cleartextTrafficPermitted="false"` を設定する）
- TLS 1.2 以上を使用する
- サーバー証明書を適切に検証する（独自の TrustManager で検証をスキップしない）
- リリースビルドでユーザー証明書を信頼しない
- 高リスクな通信には証明書ピニングを検討する

---

## MASVS-CODE — コード品質

> 参照: [MASVS-CODE-1](https://mas.owasp.org/MASVS/controls/MASVS-CODE-1/) / [MASVS-CODE-2](https://mas.owasp.org/MASVS/controls/MASVS-CODE-2/) / [MASVS-CODE-3](https://mas.owasp.org/MASVS/controls/MASVS-CODE-3/) / [MASVS-CODE-4](https://mas.owasp.org/MASVS/controls/MASVS-CODE-4/)

セキュアコーディングの原則に従い、既知の脆弱性を持つコンポーネントを使用しない。

- `libs.versions.toml` の依存ライブラリを定期的に更新し、既知の脆弱性を解消する
- リリースビルドで `isMinifyEnabled = true` を維持する（R8 による難読化・コード縮小）
- 外部入力は必ずバリデーションする
- デバッグ機能（`debuggable="true"` 等）をリリースビルドで有効にしない
- `minSdk` は合理的な範囲で最新に保ち、パッチ適用済み API を活用する

---

## MASVS-RESILIENCE — 改ざん耐性・リバースエンジニアリング対策

> 参照: [MASVS-RESILIENCE-1](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-1/) / [MASVS-RESILIENCE-2](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-2/) / [MASVS-RESILIENCE-3](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-3/) / [MASVS-RESILIENCE-4](https://mas.owasp.org/MASVS/controls/MASVS-RESILIENCE-4/)

高リスクなアプリ（金融・DRM・高価値 IP を持つアプリ）では以下を検討する。現時点では必須ではないが、機密性の高い機能を追加する際に適用を検討すること。

- **デバイス整合性の確認（MASVS-RESILIENCE-1）** — root 化・カスタム ROM 等の安全でない環境を検知し、リスクに応じた対応（警告表示・機能制限等）を行う。[Play Integrity API](https://developer.android.com/google/play/integrity) の利用を検討する
- **デバッグ防止（MASVS-RESILIENCE-2）** — リリースビルドでデバッガーのアタッチを検知する。`android:debuggable="false"` を維持する（AGP がリリースビルドで自動設定）
- **実行環境の検証（MASVS-RESILIENCE-3）** — エミュレーターや自動分析環境での実行を検知する。高リスクな処理を行う場合に限り対応を検討する
- **改ざん検知（MASVS-RESILIENCE-4）** — アプリのコード・リソースが署名時と一致するかを検証する。Play Integrity API の AppIntegrity により署名検証が可能

---

## MASVS-PRIVACY — プライバシー

> 参照: [MASVS-PRIVACY-1](https://mas.owasp.org/MASVS/controls/MASVS-PRIVACY-1/) / [MASVS-PRIVACY-2](https://mas.owasp.org/MASVS/controls/MASVS-PRIVACY-2/) / [MASVS-PRIVACY-3](https://mas.owasp.org/MASVS/controls/MASVS-PRIVACY-3/)

ユーザーのプライバシーを尊重し、必要最小限のデータのみを収集・処理する。

- アプリの目的に不要な個人データを収集しない
- 位置情報・連絡先・識別子等を利用する場合はユーザーへの説明と同意を得る
- 分析・クラッシュレポート SDK を導入する場合はデータ収集範囲を確認する
