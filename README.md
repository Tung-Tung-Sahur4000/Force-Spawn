# Force-Spawn

A Paper/Spigot plugin that forces every player to spawn in **The End** and blocks access to the **Overworld**.

## Supported versions

| Minecraft   | Paper API profile | Java required |
|-------------|-------------------|---------------|
| 1.20.x      | `mc-1.20`         | 17            |
| 1.21.x      | `mc-1.21` (default) | 21          |
| 1.22.x      | `mc-1.22`         | 21            |
| 1.23.x      | `mc-1.23`         | 21            |
| 1.24.x      | `mc-1.24`         | 21            |
| 1.25.x      | `mc-1.25`         | 21            |
| 1.26.x      | `mc-1.26`         | **25**        |

> Minecraft 1.26 and newer require **Java 25**. Older Java will refuse to load the server jar.

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
mvn clean package -Pmc-1.26    # Java 25 toolchain required
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
