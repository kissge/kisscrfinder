# kisscrfinder

Android app (Kotlin + Jetpack Compose).

## Requirements

- JDK 21
- Android SDK (compileSdk 37.2) — Android Studio を使うなら自動で揃う

## Commands

```sh
./gradlew assembleDebug          # デバッグ APK をビルド
./gradlew installDebug           # 接続中の端末/エミュレータにインストール
./gradlew testDebugUnitTest      # ユニットテスト
./gradlew connectedDebugAndroidTest  # 端末上の UI テスト
./gradlew lintDebug              # Android Lint
./gradlew spotlessApply          # ktlint で自動整形 (CI は spotlessCheck)
```

## Stack

| 項目 | 内容 |
| --- | --- |
| 言語 | Kotlin (AGP 9 の built-in Kotlin) |
| UI | Jetpack Compose + Material 3 (Compose BOM) |
| 永続化 | Room (KSP)。スキーマは `app/schemas/` に出力 |
| ビルド | Gradle Kotlin DSL + Version Catalog (`gradle/libs.versions.toml`) |
| SDK | minSdk 26 / targetSdk 37 |
| 整形 | Spotless + ktlint (`.editorconfig`) |
| CI | GitHub Actions (`.github/workflows/ci.yml`) |
| 依存更新 | Dependabot (`.github/dependabot.yml`) |

## 部首データ

`app/src/main/assets/radicals.bin` は Unicode Unihan データベースの `kRSUnicode`（康熙部首番号）から生成しています（© Unicode, Inc., [Unicode License v3](https://www.unicode.org/license.txt)）。再生成するには:

```sh
python3 -I scripts/generate_radicals.py
```

## 実機へのインストール（GitHub Actions のビルド）

main への push ごとに CI が署名付きの release APK をビルドします。`v*` タグを push すると GitHub Release にも添付されます。

### 初回だけ: 署名鍵の準備

```sh
brew install gh && gh auth login
JAVA_HOME=/opt/homebrew/opt/openjdk@21 scripts/setup-signing.sh
```

- 鍵 `~/.android/kisscrfinder-release.jks` とパスワード入りの `keystore.properties`（gitignore 済み）を作り、GitHub Secrets（`SIGNING_KEYSTORE_BASE64`, `SIGNING_PASSWORD`）に登録します。
- **この2ファイルはパスワードマネージャー等にバックアップしてください。** 失うと、インストール済みのアプリを更新できなくなります（アンインストールが必要になり、ログも消えます）。

### APK の入手

- **スマホから:** `v0.1.0` などのタグを push → リポジトリの Releases ページから APK を直接ダウンロード
- **PC から:** Actions → 実行結果 → Artifacts の `kisscrfinder-apk-<番号>` をダウンロード・展開し、`adb install -r kisscrfinder-<番号>.apk`

### 端末での設定

1. 提供元不明のアプリのインストールを許可してインストール
2. Android 13 以降: 設定 → アプリ → kisscrfinder → 右上メニュー →「制限付き設定を許可」
3. アプリを開き、ユーザー補助と通知を有効にする

ローカルの debug ビルドはアプリ ID が `do_.eki.yo.kisscrfinder.debug` なので、CI 版と同じ端末に共存できます。
