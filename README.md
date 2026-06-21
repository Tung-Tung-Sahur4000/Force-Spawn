# Force-Spawn

A Paper/Spigot plugin (1.20+) that forces every player to spawn in **The End** and blocks access to the **Overworld**.

## What it does

- On join: teleports the player to The End spawn (builds a small obsidian platform if needed).
- On respawn: respawn location is set to The End.
- On portal / teleport / world-change: any attempt to enter the Overworld is cancelled and the player is sent back to The End.
- Bed/anchor spawn point is overridden to The End on join.

## Build

```bash
mvn clean package
```

The jar will be at `target/ForceSpawn-1.0.0.jar`. Drop it into your server's `plugins/` folder.

## Config (`plugins/ForceSpawn/config.yml`)

```yaml
end-world-name: world_the_end
overworld-world-name: world
kick-overworld-message: "The Overworld is disabled on this server."
```

## Notes

- The Overworld world still loads (Minecraft requires a main world), but players cannot stay in it.
- To fully unload the Overworld from disk you would need a multiverse-style world manager; this plugin focuses on keeping players out.
