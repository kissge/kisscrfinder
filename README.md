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
