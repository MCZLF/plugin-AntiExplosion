/*
 * @Date: 2023-09-21 18:46:29
 * @LastEditors: MemoryShadow
 * @LastEditTime: 2023-09-25 21:59:37
 * @Description: 一份对当前事件的配置
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
     * 根据事件设置本类, 协助判断是否应该取消本次爆炸
     * 
     * @param e
     */
    public EventConfig(final EntityEvent e) {
        // 加载事件中的坐标信息
        Location location = e.getEntity().getLocation();
        this.X = (int) (location.getX() + .5);
        this.Y = (int) (location.getY() + .5);
        this.Z = (int) (location.getZ() + .5);
        // 加载配置信息并初始化基础信息
        Cluster config = AntiExplosionConfig.GetAntiExplosionConfig().valueOf(this.X, this.Y, this.Z);
        if (config == null) {
            // 如果没有搜索到, 就直接使用全局配置
            AntiExplosionConfig temp = AntiExplosionConfig.GetAntiExplosionConfig();
            this.Enable = temp.isEnable();
            this.Hurt = temp.isHurt();
        } else {
            this.Enable = config.isEnable();
            this.Hurt = config.isHurt();
        }
        // 检查事件是否与配置文件中的事件相匹配, 取消掉不匹配的事件
        switch (e.getEventName()) {
            case "EntityExplodeEvent":
                if (!this.Hurt) {
                    this.Enable = false;
                    return;
                }
                break;

            case "ExplosionPrimeEvent":
                if (this.Hurt) {
                    this.Enable = false;
                    return;
                }
                break;
            default:
                return;
        }
    }
}
