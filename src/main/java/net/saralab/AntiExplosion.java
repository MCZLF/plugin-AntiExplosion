package net.saralab;

import net.saralab.events.Events;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import games.loft.model.AntiExplosionConfig;
import lombok.Getter;
import lombok.Setter;

public class AntiExplosion extends JavaPlugin implements Listener {
    
    @Getter
    @Setter
    public static AntiExplosion plugin;
    public static AntiExplosionConfig config;

    public void onEnable() {
        plugin = this;
        getLogger().info("AntiExplosion is enabled!");
        PluginDescriptionFile VarUtilType = this.getDescription();
        getLogger().info("AntiExplosion V" + VarUtilType.getVersion() + " starting...");
        getServer().getPluginManager().registerEvents(new Events(), this);
        plugin.saveDefaultConfig();
        
        // 加载配置文件到缓存中
        AntiExplosion.config = AntiExplosionConfig.GetAntiExplosionConfig(plugin.getConfig());

        getLogger().info("AntiExplosion V" + VarUtilType.getVersion() + " started!");
    }
}
