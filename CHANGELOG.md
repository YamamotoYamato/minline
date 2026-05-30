# Changelog

## 0.1.4 - 2026-05-31

### Changed

- Removed automatic Windows IME context enable/disable handling.
- Limited the mod scope to inline rendering of Windows IME composition text.

### Fixed

- Suppressed the default Windows IME composition window while Minline renders inline composition text.
- Fixed inline composition position in non-chat text fields.

## 0.1.3 - 2026-05-30

### Fixed

- Fixed IME restoration after returning from external applications while Minecraft is in fullscreen.
- Reapplied Windows foreground and focus state before enabling IME for the Minecraft window.

## 0.1.2 - 2026-05-30

### Fixed

- Fixed IME restoration after returning to Minecraft while an inventory search field is focused.
- Kept IME control paused while the Minecraft window is unfocused, preventing focused text field state from expiring in the background.

## 0.1.1 - 2026-05-29

### Fixed

- Limited IME enabling on inventory-like handled screens to focused text fields, preventing inventory controls from being interrupted by an active Japanese IME.

## 0.1.0 - 2026-05-29

### Added

- Added inline rendering for Windows IME composition text in Minecraft text fields.
- Added automatic Windows IME context disabling during gameplay, so gameplay keys are less likely to be consumed by the Japanese IME.
- Added support for Minecraft 1.21.11 and Fabric Loader 0.19.2.
- Added a client-only implementation that does not require Fabric API.
