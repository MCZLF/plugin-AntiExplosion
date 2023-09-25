/*
 * @Date: 2023-09-25 14:50:54
 * @LastEditors: MemoryShadow
 * @LastEditTime: 2023-09-25 22:05:37
 * @Description: AntiExplosion的配置处理类
 * Copyright (c) 2023 by MemoryShadow@outlook.com, All Rights Reserved.
 */
package games.loft.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.configuration.file.FileConfiguration;

import lombok.Getter;
import lombok.Setter;
import net.saralab.AntiExplosion;

public class AntiExplosionConfig {

    private static AntiExplosionConfig live = null;
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
     * 当存在实例时就返回已存在的实例, 否则创建新的实例
     * 
     * @return 一个AntiExplosionConfig实例
     */
    public static AntiExplosionConfig GetAntiExplosionConfig() {
        if (live != null) {
            return live;
        } else {
            return new AntiExplosionConfig();
        }
    }

    /**
     * 当存在实例时就返回已存在的实例, 否则按照目标对象创建新的实例
     * 
     * @return 一个AntiExplosionConfig实例
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
            LinkedHashMap<String, ?> region = (LinkedHashMap<String, ?>) region_Object;
            boolean Enable = region.containsKey("Enable") ? (boolean) region.get("Enable") : this.Enable;
            boolean Hurt = region.containsKey("Hurt") ? (boolean) region.get("Hurt") : this.Hurt;

            // 将配置写入到选区中
            ArrayList<Precinct3d> precinct3ds = new ArrayList<Precinct3d>();
            for (LinkedHashMap<String, Integer> Select : (ArrayList<LinkedHashMap<String, Integer>>) region
                    .get("Select")) {
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
    }

    /**
     * 检查给定坐标是否在本选区内
     */
    public boolean isInside(int X, int Y, int Z) {
        for (Cluster cluster : regions) {
            if (cluster.isInside(X, Y, Z)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 非贪婪模式检查点是否在选区内
     * 
     * @return 当匹配成功将返回一个Cluster对象, 否则返回null
     */
    public Cluster valueOf(int X, int Y, int Z) {
        for (Cluster cluster : regions) {
            if (cluster.isInside(X, Y, Z))
                return cluster;
        }
        return null;
    }

    /**
     * 贪婪模式检查点是否在选区内
     * 
     * @return 返回一个Cluster列表
     */
    public ArrayList<Cluster> valueOf(int X, int Y, int Z, boolean voracity) {
        ArrayList<Cluster> RetObj = new ArrayList<Cluster>();
        for (Cluster cluster : regions) {
            if (cluster.isInside(X, Y, Z))
                RetObj.add(cluster);
        }
        return RetObj;
    }
}
