# Minline

[日本語版](README_JA.md)

Minline is a Minecraft 1.21.11 Fabric client mod that improves Japanese input on Windows.

## Installation

1. Download the latest jar from [Releases](https://github.com/YamamotoYamato/minline/releases).
2. Install Fabric Loader for Minecraft 1.21.11.
3. Put the downloaded `minline-0.1.1.jar` into your Minecraft `mods` folder.
4. Start Minecraft with the Fabric profile.

## Usage

Type Japanese text in chat or another Minecraft text field using a Windows IME. While conversion is active, the composition text is rendered at the cursor position with an underline. Confirmed text is handled by Minecraft and GLFW as usual.

During gameplay, Minline detaches the Windows IME context from the Minecraft window. This helps prevent the active Japanese IME from eating movement and gameplay keys. When a Minecraft screen such as chat is open, the default IME context is restored.

Inventory-like screens enable IME only while a text field is focused, so normal inventory controls are not interrupted.

## Notes

- This mod is client-side only.
- Fabric API is not required.
- IME composition rendering and gameplay-key protection currently support Windows only.
- On other operating systems, the mod loads but does not display IME composition text.

## Build

Java 21 is required.

```powershell
./gradlew.bat build
```

The jar is generated in `build/libs/`.

## License

This project is released under the Unlicense. See `LICENSE` for details.

## Changelog

See `CHANGELOG.md`.
