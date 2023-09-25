/*
 * @Date: 2023-09-25 08:48:59
 * @LastEditors: MemoryShadow
 * @LastEditTime: 2023-09-25 18:52:44
 * @Description: 簇的存储格式
 * Copyright (c) 2023 by MemoryShadow@outlook.com, All Rights Reserved.
 */
package games.loft.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Cluster {
    public enum WorldType {
        Nether(-1),
        World(0),
        End(1);

        private int value = 0;

        private WorldType(int value) {
            this.value = value;
        }

        public static WorldType valueOf(int value) {
            switch (value) {
                case -1:
                    return Nether;
                case 0:
                    return World;
                case 1:
                    return End;

                default:
                    return World;
            }
        }

        public int value() {
            return this.value;
        }
    };

    boolean Enable = true;
    boolean Hurt = true;
    String Name;
    WorldType World;
    List<Precinct3d> Select;

    public Cluster(boolean Enable, boolean Hurt, String Name, WorldType World, List<Precinct3d> Select) {
        this.Enable = Enable;
        this.Hurt = Hurt;
        this.Name = Name;
        this.World = World;
        this.Select = Select;
    }

    public Cluster(boolean Enable, boolean Hurt, String Name, String World, List<Precinct3d> Select) {
        this.Enable = Enable;
        this.Hurt = Hurt;
        this.Name = Name;
        this.World = WorldType.valueOf(World);
        this.Select = Select;
    }

    public String toString() {
        String RetStr = "Enable: " + this.Enable + "\nHurt: " + this.Hurt + "\nName: " + this.Name + "\nWorld: "
                + this.World.toString();
        if (Select.size() > 0) {
            String region = "\nregion: \n";
            for (Precinct3d precinct3d : Select) {
                region += "  " + precinct3d.toString() + "\n";
            }
            RetStr += region;
        }
        return RetStr;
    }

    /**
     * 检查给定坐标是否在本选区内
     */
    public boolean isInside(int X, int Y, int Z) {
        for (Precinct3d precinct3d : Select) {
            if (precinct3d.isInside(X, Y, Z))
                return true;
        }
        return false;
    }
}
