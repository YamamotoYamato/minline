# Minline

Minline は Minecraft 1.21.11 / Fabric 用のクライアント mod です。Windows での日本語入力を改善します。

## インストール

1. [Releases](https://github.com/YamamotoYamato/minline/releases) から最新の jar をダウンロードします。
2. Minecraft 1.21.11 用の Fabric Loader をインストールします。
3. ダウンロードした `minline-0.1.1.jar` を Minecraft の `mods` フォルダに入れます。
4. Fabric プロファイルで Minecraft を起動します。

## 使い方

チャット欄などのテキスト入力欄で Windows IME を使って日本語を入力します。変換中の未確定文字列が、カーソル位置に下線つきで表示されます。確定済み文字の入力処理は Minecraft / GLFW 標準の挙動を使います。

ゲーム操作中は、Minecraft ウィンドウから Windows IME のコンテキストを外します。これにより、日本語 IME が有効なままでも移動などの操作キーが IME に奪われにくくなります。チャットなどの Minecraft 画面を開いている間は、既定の IME コンテキストを戻します。

インベントリ系の画面では、テキスト入力欄にフォーカスしている間だけ IME を有効化するため、通常のインベントリ操作を妨げにくくなっています。

## 注意

- クライアント専用 mod です。
- Fabric API には依存しません。
- IME 未確定文字列のインライン表示と、操作キー保護は現在 Windows 専用です。
- Windows 以外では mod は読み込まれますが、IME 未確定文字列は表示されません。

## ビルド

Java 21 が必要です。

```powershell
./gradlew.bat build
```

jar は `build/libs/` に生成されます。

## ライセンス

このプロジェクトは MIT License で公開されています。詳細は `LICENSE` を参照してください。

## 変更履歴

`CHANGELOG.md` を参照してください。
