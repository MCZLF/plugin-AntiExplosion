/*
 * @Date: 2023-09-25 09:32:55
 * @LastEditors: MemoryShadow
 * @LastEditTime: 2023-09-25 18:42:36
 * @Description: 选区3D
 * Copyright (c) 2023 by MemoryShadow@outlook.com, All Rights Reserved.
 */
package games.loft.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Precinct3d {
    int X;
    int ToX = 0;
    int Y;
    int ToY = 0;
    int Z;
    int ToZ = 0;
    
    public Precinct3d(int X, int Y, int Z, int ToX, int ToY, int ToZ) {
        // 优化, 将每个选区的对角坐标进行调整
        this.X = Math.min(X, ToX);
        this.ToX = Math.max(X, ToX);
        this.Y = Math.min(Y, ToY);
        this.ToY = Math.max(Y, ToY);
        this.Z = Math.min(Z, ToZ);
        this.ToZ = Math.max(Z, ToZ);
    }

    public String toString() {
        return this.X + ", " + this.Y + ", " + this.Z + " ~ " + this.ToX + ", " + this.ToY + ", " + this.ToZ;
    }

    /**
     * 检查给定坐标是否在本选区内
     */
    public boolean isInside(int X, int Y, int Z) {
        if (X >= this.X && Y >= this.Y && Z >= this.Z && X <= this.ToX && Y <= this.ToY && Z <= this.ToZ) 
            return true; else return false;
    }
}
