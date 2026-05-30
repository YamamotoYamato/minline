# Minline

[日本語](README_JA.md)

Minline is a Minecraft 1.21.11 Fabric client mod that displays Windows IME composition text inline in Minecraft text fields.

## Installation

1. Download the latest jar from [Releases](https://github.com/YamamotoYamato/minline/releases).
2. Install Fabric Loader for Minecraft 1.21.11.
3. Put the downloaded `minline-0.1.5.jar` into your Minecraft `mods` folder.
4. Start Minecraft with the Fabric profile.

## Usage

Type Japanese text in chat or another Minecraft text field using a Windows IME. While conversion is active, the composition text is rendered at the cursor position with an underline. Confirmed text is handled by Minecraft and GLFW as usual.

When candidates are available, Minline renders a simple candidate list near the text field. Pressing `Esc` closes the screen and also asks Windows to switch the IME back to direct input.

Minline does not change the IME on/off state. Switch IME normally with your keyboard or operating system settings.

## Notes

- This mod is client-side only.
- Fabric API is not required.
- IME composition rendering currently supports Windows only.
- On other operating systems, the mod loads but does not display IME composition text.

## Build

Java 21 is required.

```powershell
./gradlew.bat build
```

The jar is generated in `build/libs/`.

## License

This project is released under the MIT License. See `LICENSE` for details.

## Changelog

See `CHANGELOG.md`.
