package com.forcespawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ForceSpawnPlugin extends JavaPlugin implements Listener {

    private String endWorldName;
    private String overworldName;
    private String kickMessage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        endWorldName = getConfig().getString("end-world-name", "world_the_end");
        overworldName = getConfig().getString("overworld-world-name", "world");
        kickMessage = getConfig().getString("kick-overworld-message", "The Overworld is disabled on this server.");

        getServer().getPluginManager().registerEvents(this, this);
        checkRuntime();
        getLogger().info("ForceSpawn enabled. End world: " + endWorldName + ", blocked Overworld: " + overworldName);
    }

    private void checkRuntime() {
        String mcVersion = Bukkit.getBukkitVersion(); // e.g. "1.21.4-..." (legacy) or "26.1-..." (year-based)
        int javaMajor = Runtime.version().feature();
        getLogger().info("Detected Minecraft " + mcVersion + " on Java " + javaMajor);

        int[] parsed = parseVersion(mcVersion);
        int major = parsed[0];
        int minor = parsed[1];
        boolean yearBased = major >= 26; // new scheme starts at 26.x

        if (yearBased && javaMajor < 25) {
            getLogger().severe("Minecraft " + major + ".x requires Java 25 or newer. Current: Java " + javaMajor);
        } else if (!yearBased && major == 1 && minor >= 21 && javaMajor < 21) {
            getLogger().severe("Minecraft 1.21+ requires Java 21 or newer. Current: Java " + javaMajor);
        }
    }

    private int[] parseVersion(String bukkitVersion) {
        try {
            String[] parts = bukkitVersion.split("-")[0].split("\\.");
            int a = parts.length >= 1 ? Integer.parseInt(parts[0]) : 0;
            int b = parts.length >= 2 ? Integer.parseInt(parts[1]) : 0;
            return new int[] { a, b };
        } catch (NumberFormatException e) {
            return new int[] { 0, 0 };
        }
    }

    private World getEndWorld() {
        World end = Bukkit.getWorld(endWorldName);
        if (end == null) {
            for (World w : Bukkit.getWorlds()) {
                if (w.getEnvironment() == World.Environment.THE_END) {
                    return w;
                }
            }
        }
        return end;
    }

    private Location safeEndSpawn(World end) {
        Location spawn = end.getSpawnLocation();
        // Build a small platform under the player if needed (avoid void fall at 0,0).
        int x = spawn.getBlockX();
        int z = spawn.getBlockZ();
        int y = Math.max(spawn.getBlockY(), 64);
        World.Environment env = end.getEnvironment();
        if (env == World.Environment.THE_END) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Block b = end.getBlockAt(x + dx, y - 1, z + dz);
                    if (b.getType().isAir()) {
                        b.setType(org.bukkit.Material.OBSIDIAN);
                    }
                    end.getBlockAt(x + dx, y, z + dz).setType(org.bukkit.Material.AIR);
                    end.getBlockAt(x + dx, y + 1, z + dz).setType(org.bukkit.Material.AIR);
                }
            }
        }
        return new Location(end, x + 0.5, y, z + 0.5);
    }

    private boolean isOverworld(World world) {
        if (world == null) return false;
        if (world.getName().equalsIgnoreCase(overworldName)) return true;
        return world.getEnvironment() == World.Environment.NORMAL;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldInit(WorldInitEvent event) {
        if (isOverworld(event.getWorld())) {
            event.getWorld().setKeepSpawnInMemory(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World end = getEndWorld();
        if (end == null) {
            getLogger().warning("End world not found! Player " + player.getName() + " stays in current world.");
            return;
        }
        if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
            player.teleport(safeEndSpawn(end));
        }
        player.setBedSpawnLocation(safeEndSpawn(end), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        World end = getEndWorld();
        if (end != null) {
            event.setRespawnLocation(safeEndSpawn(end));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        Location to = event.getTo();
        if (to != null && isOverworld(to.getWorld())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(kickMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPortal(EntityPortalEvent event) {
        Location to = event.getTo();
        if (to != null && isOverworld(to.getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Location to = event.getTo();
        if (to != null && isOverworld(to.getWorld())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(kickMessage);
            World end = getEndWorld();
            if (end != null && event.getPlayer().getWorld().getEnvironment() != World.Environment.THE_END) {
                event.getPlayer().teleport(safeEndSpawn(end));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (isOverworld(player.getWorld())) {
            World end = getEndWorld();
            if (end != null) {
                player.teleport(safeEndSpawn(end));
                player.sendMessage(kickMessage);
            }
        }
    }
}
