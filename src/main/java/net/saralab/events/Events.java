package net.saralab.events;

import org.bukkit.event.*;
import org.bukkit.event.entity.*;

import games.loft.model.EventConfig;

public class Events implements Listener {

    @EventHandler(ignoreCancelled = false)
    public void onEntityExplode(final EntityExplodeEvent e) {
        EventConfig eventConfig = new EventConfig(e);
        if (eventConfig.isEnable()) {
            e.blockList().clear();
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onExplosionPrimeEvent(final ExplosionPrimeEvent e) {
        EventConfig eventConfig = new EventConfig(e);
        if (eventConfig.isEnable()) {
            e.setCancelled(true);
        }
    }
}