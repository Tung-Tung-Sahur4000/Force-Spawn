# Force-Spawn

A Paper/Spigot plugin that forces every player to spawn in **The End** and blocks access to the **Overworld**.

## Supported versions

| Minecraft               | Paper API profile     | Paper artifact                     | Java |
|-------------------------|-----------------------|------------------------------------|------|
| 1.20.x                  | `mc-1.20`             | `1.20.6-R0.1-SNAPSHOT`             | 17   |
| **1.21.11** (stable, default) | `mc-1.21`        | `1.21.11-R0.1-SNAPSHOT`            | 21   |
| 1.22.x                  | `mc-1.22`             | `1.22-R0.1-SNAPSHOT`               | 21   |
| 1.23.x                  | `mc-1.23`             | `1.23-R0.1-SNAPSHOT`               | 21   |
| 1.24.x                  | `mc-1.24`             | `1.24-R0.1-SNAPSHOT`               | 21   |
| 1.25.x                  | `mc-1.25`             | `1.25-R0.1-SNAPSHOT`               | 21   |
| **26.1.2** (stable)     | `mc-26.1`             | `26.1.2-R0.1-SNAPSHOT`             | **25** |
| 26.2.x (alpha)          | `mc-26.2`             | `26.2-R0.1-SNAPSHOT`               | **25** |

> Mojang moved to a **year-based** version scheme. After 1.25.x the next release is **26.1** ([announcement](https://papermc.io/news/26-1) · [1.21.11 + 26 changes](https://papermc.io/news/1-21-11/)).
> Confirmed stable on the [Paper fill API](https://fill.papermc.io/v3/projects/paper/versions): **1.21.11** and **26.1.2** (SUPPORTED). 26.1.1 is UNSUPPORTED.

### 26.1 notes that affect plugin authors

- **Unobfuscated server jars** — the internal remapper is gone. Plugins reaching into `net.minecraft.*` must ship Mojang-mapped code. *Force-Spawn only uses Bukkit API, so it is unaffected.*
- **`WorldInfo#getName` deprecated for identity** — use `getKey()`. Handled here: `isOverworld()` checks `World#getKey()` (`minecraft:overworld`) first, with a `NoSuchMethodError` fallback to the configured name on older APIs.
- **Gamerules switched to snake_case registry** — not used by this plugin.
- **WorldBorder duration in ticks** — not used by this plugin.

## What it does

- On join: teleports the player to The End spawn (builds a small obsidian platform so they don't void out).
- On respawn: respawn location is set to The End.
- On portal / teleport / world-change: any attempt to enter the Overworld is cancelled and the player is sent back to The End.
- Bed/anchor spawn point is overridden to The End on join.
- On enable: logs a warning if the running Java version is too old for the detected Minecraft version.

## Build

Default (1.21.x, Java 21):

```bash
mvn clean package
```

Build for a specific Minecraft line — pick the profile:

```bash
mvn clean package -Pmc-1.20    # Java 17
mvn clean package -Pmc-1.22    # Java 21
mvn clean package -Pmc-1.25    # Java 21
mvn clean package -Pmc-26.1    # year-based 26.1.x stable, Java 25 toolchain
mvn clean package -Pmc-26.2    # year-based 26.2.x alpha,  Java 25 toolchain
```

The jar will be at `target/ForceSpawn-1.1.0-mc<version>.jar`. Drop it into your server's `plugins/` folder.

## Config (`plugins/ForceSpawn/config.yml`)

```yaml
end-world-name: world_the_end
overworld-world-name: world
kick-overworld-message: "The Overworld is disabled on this server."
```

## Notes

- The Overworld world still loads at the server level (Minecraft requires a primary world), but players cannot stay in it.
- To fully remove the Overworld from disk, use a world manager like Multiverse-Core in addition to this plugin.
