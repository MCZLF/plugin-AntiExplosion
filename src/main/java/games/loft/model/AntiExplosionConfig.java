/*
 * @Date: 2023-09-25 14:50:54
 * @LastEditors: MemoryShadow
 * @LastEditTime: 2026-03-15 17:00:00
 * @Description: AntiExplosion configuration handler with chunk-based spatial index
 * Copyright (c) 2023 by MemoryShadow@outlook.com, All Rights Reserved.
 */
package games.loft.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import lombok.Getter;
import lombok.Setter;
import net.saralab.AntiExplosion;

public class AntiExplosionConfig {

    private static AntiExplosionConfig live = null;

    /**
     * L1 cache: stores the last successfully matched Cluster to accelerate
     * consecutive lookups during chain explosions (spatial locality).
     * Marked volatile to ensure cross-thread visibility on the main server thread.
     */
    private static volatile Cluster lastHitCache = null;

    @Getter
    @Setter
    private boolean Enable = true;
    @Getter
    @Setter
    private boolean Hurt = true;
    @Getter
    @Setter
    private ArrayList<Cluster> regions = new ArrayList<Cluster>();

    /**
     * L2 index: chunk-based spatial hash map.
     * Key = packed chunk coordinate (chunkX << 32 | chunkZ).
     * Value = list of Clusters whose bounding boxes overlap this chunk.
     */
    private HashMap<Long, List<Cluster>> chunkIndex = new HashMap<Long, List<Cluster>>();

    /**
     * Return the existing singleton instance, or create a new one from the
     * plugin's default config if none exists yet.
     *
     * @return an AntiExplosionConfig instance
     */
    public static AntiExplosionConfig GetAntiExplosionConfig() {
        if (live != null) {
            return live;
        } else {
            return new AntiExplosionConfig();
        }
    }

    /**
     * Return the existing singleton instance, or create a new one from the
     * given FileConfiguration if none exists yet.
     *
     * @return an AntiExplosionConfig instance
     */
    public static AntiExplosionConfig GetAntiExplosionConfig(FileConfiguration config) {
        if (live != null) {
            return live;
        } else {
            return new AntiExplosionConfig(config);
        }
    }

    public String toString() {
        return "global:\n  Enable: " + this.Enable + "\n  Hurt: " + this.Hurt + "\nregions: " + regions;
    }

    private AntiExplosionConfig() {
        this(AntiExplosion.getPlugin().getConfig());
    }

    private AntiExplosionConfig(FileConfiguration config) {
        this.Enable = config.getBoolean("global.Enable", true);
        this.Hurt = config.getBoolean("global.Hurt", true);
        for (Object region_Object : config.getList("regions")) {
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, ?> region = (LinkedHashMap<String, ?>) region_Object;
            boolean Enable = region.containsKey("Enable") ? (Boolean) region.get("Enable") : this.Enable;
            boolean Hurt = region.containsKey("Hurt") ? (Boolean) region.get("Hurt") : this.Hurt;

            // Parse precinct selections from config
            ArrayList<Precinct3d> precinct3ds = new ArrayList<Precinct3d>();
            @SuppressWarnings("unchecked")
            ArrayList<LinkedHashMap<String, Integer>> selectList = (ArrayList<LinkedHashMap<String, Integer>>) region.get("Select");
            for (LinkedHashMap<String, Integer> Select : selectList) {
                precinct3ds.add(new Precinct3d(
                        Select.get("X"),
                        Select.get("Y"),
                        Select.get("Z"),
                        Select.get("ToX"),
                        Select.get("ToY"),
                        Select.get("ToZ")));
            }
            Cluster cluster = new Cluster(Enable, Hurt, (String) region.get("Name"), region.get("World").toString(),
                    precinct3ds);
            this.regions.add(cluster);
        }

        // Build the chunk-based spatial index after all regions are loaded
        buildIndex();

        // Fix: assign singleton reference so subsequent calls reuse this instance
        live = this;
    }

    /**
     * Build the L2 chunk-based spatial index.
     * For each Precinct3d in every Cluster, compute all chunk coordinates
     * covered by the bounding box (XZ plane) and register the Cluster
     * reference into the corresponding hash bucket.
     */
    private void buildIndex() {
        chunkIndex.clear();
        for (Cluster cluster : regions) {
            for (Precinct3d precinct : cluster.getSelect()) {
                // Calculate chunk coordinate range covered by this precinct (XZ plane)
                int minCX = precinct.getX() >> 4;
                int maxCX = precinct.getToX() >> 4;
                int minCZ = precinct.getZ() >> 4;
                int maxCZ = precinct.getToZ() >> 4;
                for (int cx = minCX; cx <= maxCX; cx++) {
                    for (int cz = minCZ; cz <= maxCZ; cz++) {
                        long key = packChunkKey(cx, cz);
                        chunkIndex.computeIfAbsent(key, k -> new ArrayList<>()).add(cluster);
                    }
                }
            }
        }
    }

    /**
     * Pack two chunk coordinates into a single long key.
     * Upper 32 bits = chunkX, lower 32 bits = chunkZ (unsigned).
     */
    private static long packChunkKey(int chunkX, int chunkZ) {
        return (long) chunkX << 32 | (chunkZ & 0xFFFFFFFFL);
    }

    /**
     * Check whether the given coordinate falls inside any registered region.
     */
    public boolean isInside(int X, int Y, int Z) {
        return valueOf(X, Y, Z) != null;
    }

    /**
     * Non-greedy search: find the first Cluster that contains the given point.
     * Search order: L1 cache -> L2 chunk index -> null (global fallback).
     *
     * @return the matching Cluster, or null if no region contains the point
     */
    public Cluster valueOf(int X, int Y, int Z) {
        // L1 cache: check the last successfully hit Cluster first
        Cluster cached = lastHitCache;
        if (cached != null && cached.isInside(X, Y, Z)) {
            return cached;
        }

        // L2 index: narrow candidates down to the target chunk
        long key = packChunkKey(X >> 4, Z >> 4);
        List<Cluster> candidates = chunkIndex.get(key);
        if (candidates == null) {
            return null;
        }

        // Precise AABB check on the narrowed candidate set
        for (Cluster cluster : candidates) {
            if (cluster.isInside(X, Y, Z)) {
                // Update L1 cache for subsequent chain explosions
                lastHitCache = cluster;
                return cluster;
            }
        }
        return null;
    }

    /**
     * Greedy search: find ALL Clusters that contain the given point.
     * Uses L2 chunk index to narrow candidates before precise checks.
     *
     * @return a list of matching Clusters (may be empty, never null)
     */
    public ArrayList<Cluster> valueOf(int X, int Y, int Z, boolean voracity) {
        ArrayList<Cluster> result = new ArrayList<Cluster>();

        // L2 index: narrow candidates down to the target chunk
        long key = packChunkKey(X >> 4, Z >> 4);
        List<Cluster> candidates = chunkIndex.get(key);
        if (candidates == null) {
            return result;
        }

        // Precise AABB check on the narrowed candidate set
        for (Cluster cluster : candidates) {
            if (cluster.isInside(X, Y, Z)) {
                result.add(cluster);
            }
        }

        // Update L1 cache with the first match if available
        if (!result.isEmpty()) {
            lastHitCache = result.get(0);
        }
        return result;
    }
}
