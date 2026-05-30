# Minline

[English](README.md)

Minline は Minecraft 1.21.11 / Fabric 用のクライアント mod です。Windows IME の未確定文字列を Minecraft のテキスト入力欄にインライン表示します。

## インストール

1. [Releases](https://github.com/YamamotoYamato/minline/releases) から最新の jar をダウンロードします。
2. Minecraft 1.21.11 用の Fabric Loader をインストールします。
3. ダウンロードした `minline-0.1.5.jar` を Minecraft の `mods` フォルダに入れます。
4. Fabric プロファイルで Minecraft を起動します。

## 使い方

チャット欄などのテキスト入力欄で Windows IME を使って日本語を入力します。変換中の未確定文字列が、カーソル位置に下線つきで表示されます。確定済み文字の入力処理は Minecraft / GLFW 標準の挙動を使います。

候補が取得できる場合は、テキスト入力欄の近くに簡易候補一覧を表示します。`Esc` を押すと画面を閉じると同時に、Windows に IME を直接入力へ戻すよう要求します。

Minline は IME のオン / オフ状態を変更しません。IME の切り替えは、通常どおりキーボードや OS の設定で行ってください。

## 注意

- クライアント専用 mod です。
- Fabric API には依存しません。
- IME 未確定文字列のインライン表示は現在 Windows 専用です。
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
