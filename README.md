# IPN Extras

A multi-version Fabric mod that enhances Inventory Profiles Next (IPN) compatibility with GCA (Gaming Chair Addon) fake player containers by restricting sorting operations to valid inventory slots.

## Features

This mod intelligently filters IPN sorting operations in GCA containers:

- **Slot Filtering**: Restricts IPN sorting to specific slots in GCA containers
- **Preserves UI**: Prevents IPN buttons from interfering with GCA's custom interface elements  
- **Multi-Version Support**: Compatible with MC 1.20.1 through 26.1 using ReplayMod Preprocessor

## Problem Solved

GCA fake player containers have special slot layouts:
- **Fake Player Inventory**: Slots 0-17 contain buttons/armor, only slots 18-53 should be sorted
- **Fake Player Ender Chest**: Slots 0-26 contain ender chest slots, only slots 27-53 should be sorted

Without this mod, IPN would attempt to sort all slots including the special control/armor slots, causing inventory corruption.

## Technical Implementation

### Slot Filtering via Mixin

The mod intercepts IPN's `GeneralInventoryActions.InnerActions.innerDoSort` method and filters the ItemArea's slotIndices list before sorting executes:

```java
@Inject(method = "innerDoSort", at = @At(value = "INVOKE", target = "...SubTracker.sort(...)"))
private static void onInnerDoSort(..., ItemArea target) {
    ContainerCategory category = GcaContainerDetector.detectGcaContainer(container);
    if (category == GCA_FAKE_PLAYER_INVENTORY) {
        target.slotIndices.removeIf(slot -> slot < 18 || slot > 53);
    } else if (category == GCA_FAKE_PLAYER_ENDER_CHEST) {
        target.slotIndices.removeIf(slot -> slot < 27 || slot > 53);
    }
}
```

### GCA Container Detection

Containers are identified by checking slot 0's NBT data:
- Must be `ChestMenu` type
- Slot 0 must have NBT tag `GcaClear` or `gca.clear`
- Container type determined by screen title keywords

### Multi-Version Support

Uses ReplayMod Preprocessor with version-specific code:
- **MC 1.20.1**: Uses legacy `ItemStack.getTag()` API
- **MC 1.20.5+**: Uses new `DataComponents.CUSTOM_DATA` API

## Supported Versions

| Version | Java | API Style       | Status      |
|---------|------|-----------------|-------------|
| 1.20.1  | 17   | Legacy NBT      | ✅ Supported |
| 1.20.5  | 21   | Data Components | ✅ Supported |
| 1.21.1  | 21   | Data Components | ✅ Supported |
| 26.1    | 21   | Data Components | ✅ Supported |

## Building

```bash
# Build all versions
./gradlew buildAll

# Build specific version
./gradlew :versions:1.20.1:build
./gradlew :versions:1.21.1:build
./gradlew :versions:26.1:build
```

Build outputs will be in `versions/$VERSION/build/libs/`.

## Installation

1. Install Fabric Loader
2. Install Fabric API
3. Install Inventory Profiles Next (optional but recommended)
4. Place the built jar in your `mods` folder

## Technical Details

### Mixin Targets

- **ContainerScreenEventHandler**: Hides IPN buttons in GCA containers
- **InnerActions**: Filters slot indices before sorting

### Architecture

```
common/           → Shared enum definitions
  ContainerCategory → GCA container types
  Pattern           → Preprocessor annotation
  
versions/mainVersion/ → Main source code with Preprocessor annotations
  GcaContainerDetector → Detects GCA containers
  mixin/
    ContainerScreenEventHandlerMixin → UI filtering
    InnerActionsMixin → Slot filtering
```

## License

MIT License

## Acknowledgments

- [ReplayMod Preprocessor](https://github.com/ReplayMod/preprocessor) for multi-version support
- [AnotherInventorySort](https://github.com/Whisperlyric/AnotherInventorySort) for reference implementation
- [Inventory Profiles Next](https://github.com/Inventory-Tweaks-Development/Inventory-Profiles-Next) for the target mod