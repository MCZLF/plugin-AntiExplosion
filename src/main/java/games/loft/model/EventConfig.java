/*
 * @Date: 2023-09-21 18:46:29
 * @LastEditors: MemoryShadow
 * @LastEditTime: 2026-03-15 17:00:00
 * @Description: Per-event configuration resolver for explosion handling
 * Copyright (c) 2023 by MemoryShadow@outlook.com, All Rights Reserved.
 */
package games.loft.model;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityEvent;

import lombok.Getter;
import lombok.Setter;
import net.saralab.AntiExplosion;

@Getter
@Setter
public class EventConfig {
    boolean Enable = true;
    boolean Hurt = true;
    int X;
    int Y;
    int Z;

    /**
     * Resolve explosion configuration based on event location.
     * Delegates spatial lookup to AntiExplosionConfig which handles
     * L1 cache and L2 chunk index internally.
     *
     * @param e the entity event triggering the explosion
     */
    public EventConfig(final EntityEvent e) {
        // Extract integer coordinates from event location
        Location location = e.getEntity().getLocation();
        this.X = (int) (location.getX() + .5);
        this.Y = (int) (location.getY() + .5);
        this.Z = (int) (location.getZ() + .5);

        // Perform spatial lookup via the optimized search chain:
        // L1 cache -> L2 chunk index -> precise AABB check
        AntiExplosionConfig antiConfig = AntiExplosionConfig.GetAntiExplosionConfig();
        Cluster config = antiConfig.valueOf(this.X, this.Y, this.Z);

        if (config == null) {
            // No region matched: fall back to global configuration
            this.Enable = antiConfig.isEnable();
            this.Hurt = antiConfig.isHurt();
        } else {
            // Region matched: use region-specific configuration
            this.Enable = config.isEnable();
            this.Hurt = config.isHurt();
        }

        /**
         * Determine whether this specific event type should be intercepted:
         *   - EntityExplodeEvent + Hurt=false => cancel (only block damage was requested)
         *   - ExplosionPrimeEvent + Hurt=true => cancel (entity damage is allowed)
         */
        if (
            (e.getEventName().equals("EntityExplodeEvent") && !this.Hurt) ||
            (e.getEventName().equals("ExplosionPrimeEvent") && this.Hurt)
            ) this.Enable = false;

        // Log the decision for debugging purposes
        if (this.Enable)
            AntiExplosion.getPlugin().getLogger().info("BoomEvent: " + this.toString() + "\n Region: " + (config != null ? config.getName() : "global"));
        else
            AntiExplosion.getPlugin().getLogger().info("AntiExplosion Cancelled" + ". Region: " + (config != null ? config.getName() : "global"));
    }

    public String toString() {
        return "EventConfig:\n  Enable: " + this.Enable +
                "\n  Hurt: " + this.Hurt +
                "\n  Loc: " + this.X + ", " + this.Y + ", " + this.Z ;
    }
}
