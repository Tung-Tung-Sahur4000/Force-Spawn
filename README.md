# Force-Spawn

A Paper/Spigot plugin that forces every player to spawn in **The End** and blocks access to the **Overworld**.

## Supported versions

The single jar runs on **every 1.21.x release (1.21 → 1.21.11)** and on **26.1.1 / 26.1.2**. UNSUPPORTED only means Paper has stopped publishing patches for that line — the API surface this plugin uses is unchanged, so the plugin keeps working.

| Minecraft           | Paper artifact                | Java | Profile          |
|---------------------|-------------------------------|------|------------------|
| 1.20.x              | `1.20.6-R0.1-SNAPSHOT`        | 17   | `mc-1.20`        |
| 1.21                | `1.21-R0.1-SNAPSHOT`          | 21   | `mc-1.21.0`      |
| 1.21.1              | `1.21.1-R0.1-SNAPSHOT`        | 21   | `mc-1.21.1`      |
| 1.21.2              | `1.21.2-R0.1-SNAPSHOT`        | 21   | `mc-1.21.2`      |
| 1.21.3              | `1.21.3-R0.1-SNAPSHOT`        | 21   | `mc-1.21.3`      |
| 1.21.4              | `1.21.4-R0.1-SNAPSHOT`        | 21   | `mc-1.21.4`      |
| 1.21.5              | `1.21.5-R0.1-SNAPSHOT`        | 21   | `mc-1.21.5`      |
| 1.21.6              | `1.21.6-R0.1-SNAPSHOT`        | 21   | `mc-1.21.6`      |
| 1.21.7              | `1.21.7-R0.1-SNAPSHOT`        | 21   | `mc-1.21.7`      |
| 1.21.8              | `1.21.8-R0.1-SNAPSHOT`        | 21   | `mc-1.21.8`      |
| 1.21.9              | `1.21.9-R0.1-SNAPSHOT`        | 21   | `mc-1.21.9`      |
| 1.21.10             | `1.21.10-R0.1-SNAPSHOT`       | 21   | `mc-1.21.10`     |
| **1.21.11** (default) | `1.21.11-R0.1-SNAPSHOT`     | 21   | `mc-1.21.11` / `mc-1.21` |
| 26.1.1              | `26.1.1.build.29-alpha`       | **25** | `mc-26.1.1`    |
| **26.1.2**          | `26.1.2.build.72-stable`      | **25** | `mc-26.1.2` / `mc-26.1` |
| 26.2.x (alpha)      | `26.2.build.26-alpha`         | **25** | `mc-26.2`      |

> Year-based Paper artifacts use a new version-string format: `<mc>.build.<n>-<channel>` (e.g. `26.1.2.build.72-stable`) instead of the legacy `-R0.1-SNAPSHOT`.

> Mojang moved to a **year-based** scheme; after 1.25.x the next release is **26.1** ([announcement](https://papermc.io/news/26-1) · [1.21.11 + 26 changes](https://papermc.io/news/1-21-11/) · [Paper fill API](https://fill.papermc.io/v3/projects/paper/versions)).

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
mvn clean package -Pmc-1.21.4  # pin to that exact 1.21 patch
mvn clean package -Pmc-26.1    # newest 26.1.x (== 26.1.2), Java 25 toolchain
mvn clean package -Pmc-26.1.1  # explicit 26.1.1
mvn clean package -Pmc-26.2    # year-based 26.2.x alpha,  Java 25 toolchain

# Or override directly without a profile:
mvn clean package -Dpaper.api.version=1.21.7-R0.1-SNAPSHOT -Dplugin.api.version=1.21 -Djava.version=21
```

The jar will be at `target/ForceSpawn-1.1.0-mc<version>.jar`. Drop it into your server's `plugins/` folder.

## Releases (CI)

A GitHub Actions workflow at [`.github/workflows/build-release.yml`](.github/workflows/build-release.yml) builds the stable targets in a matrix:

- `ForceSpawn-mc1.21.11.jar` — Paper 1.21.11, Java 21
- `ForceSpawn-mc26.1.2.jar`  — Paper 26.1.2,  Java 25

Triggers:
- **Push a `v*` tag** (e.g. `git tag v1.1.0 && git push origin v1.1.0`) → builds both jars and publishes a GitHub Release with them attached.
- **`workflow_dispatch`** → manually run; pass a `release_tag` input to also publish a Release, or leave it blank to just produce build artifacts.

## Config (`plugins/ForceSpawn/config.yml`)

```yaml
end-world-name: world_the_end
overworld-world-name: world
kick-overworld-message: "The Overworld is disabled on this server."
```

## Notes

- The Overworld world still loads at the server level (Minecraft requires a primary world), but players cannot stay in it.
- To fully remove the Overworld from disk, use a world manager like Multiverse-Core in addition to this plugin.
