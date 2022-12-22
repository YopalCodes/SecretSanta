package com.yopal.secretsanta;

import com.yopal.secretsanta.command.SantaCommand;
import com.yopal.secretsanta.listener.ServerListener;
import com.yopal.secretsanta.managers.ConfigManager;
import com.yopal.secretsanta.managers.GameControl;
import com.yopal.secretsanta.managers.MusicManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Secretsanta extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        ConfigManager.setupConfig(this);
        GameControl.setupGame(this);
        MusicManager.loadPlaylist(this);

        Bukkit.getPluginManager().registerEvents(new ServerListener(this), this);
        getCommand("secretsanta").setExecutor(new SantaCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ConfigManager.removeAllPlayers(this);

    }
}
