# VoidRoom

A lightweight Paper/Spigot plugin that prevents players from equipping elytra inside a configurable region.

Players can still keep, move, and drop elytra. Only equipping is blocked.

## Features

- Configurable cuboid region (`voidroom`)
- Fist selection system (no wand required)
- Blocks chestplate/elytra equip attempts inside the region
- Allows inventory moves and drops
- Periodic safety check
- Fully customizable messages (`message.yml`)
- Legacy color codes (`&a`) and hex colors (`&#FF5555`)
- bStats metrics

## Requirements

- Java 17+
- Paper / Pufferfish / Spigot **1.20.x** (api-version `1.20`)

## Installation

1. Build the plugin or download the jar
2. Put `VoidRoom-1.0.0.jar` into your server `plugins/` folder
3. Start the server once
4. Edit `plugins/VoidRoom/config.yml` and `plugins/VoidRoom/message.yml`
5. Run `/voidroom reload` or restart the server

## Commands

| Command | Description |
|---|---|
| `/voidroom select` | Enable/disable fist selection mode |
| `/voidroom confirm` | Confirm resetting an existing selection |
| `/voidroom pos1` | Set position 1 at your current location |
| `/voidroom pos2` | Set position 2 at your current location |
| `/voidroom save` | Save the selected region |
| `/voidroom reload` | Reload config and messages |

Permission: `voidroom.admin` (default: `op`)

## How to set the region

1. Run `/voidroom select`
2. Empty hand (fist):
   - Left click a block = `pos1`
   - Right click a block = `pos2`
3. Run `/voidroom save`

If you already have a selection and start a new one, you must confirm with `/voidroom confirm`.

After a successful save, selection mode is automatically disabled.

## Configuration

### `config.yml`

```yaml
settings:
  region-name: voidroom
  check-interval-ticks: 5
  message-cooldown-ms: 2000

regions:
  voidroom:
    world: world
    minX: -100
    minY: 0
    minZ: -100
    maxX: 100
    maxY: 320
    maxZ: 100
```

- `region-name`: region key used by the plugin
- `check-interval-ticks`: how often equipped elytra are checked
- `message-cooldown-ms`: delay between deny messages

### `message.yml`

All player-facing messages are here and support:

- Legacy colors: `&c`, `&a`, `§c`
- Hex colors: `&#FF5555`

Placeholders:

- `{x}`, `{y}`, `{z}` for position messages

## Building

```bash
mvn clean package
```

The shaded jar will be created at:

```text
target/VoidRoom-1.0.0.jar
```

## Metrics

This plugin uses [bStats](https://bstats.org/) to collect anonymous usage statistics.

Plugin ID: `33389`

You can opt out by setting `enabled: false` in `plugins/bStats/config.yml`.

## Behavior notes

Inside the region:

- Elytra **cannot** be equipped
- Elytra **can** be moved in inventory
- Elytra **can** be dropped
- If somehow equipped, they are unequipped automatically

## License

All rights reserved unless otherwise stated by the author.
