package net.saralab;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiExplosion extends JavaPlugin implements Listener {
    public void onEnable() {
        getLogger().info("AntiExplosion is enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent entityExplodeEvent) {
        entityExplodeEvent.blockList().clear();
    }
}
