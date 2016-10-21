/*
 * Copyright (C) 2016 Lupine Network <bedev@twpclan.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lupinenetwork.superspawn;

import com.lupinenetwork.superspawn.commands.SpawnCommand;
import com.lupinenetwork.superspawn.commands.SuperSpawnCommand;
import com.lupinenetwork.superspawn.database.SuperSpawnManager;
import com.lupinenetwork.superspawn.listeners.PlayerSpawnedListener;
import com.lupinenetwork.superspawn.util.C;
import com.lupinenetwork.superspawn.util.SQLUtil;
import java.sql.Driver;
import org.bukkit.configuration.Configuration;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Majora320 &lt;Majora320@gmail.com&gt;
 */
public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        Configuration config = getConfig();
        
        String driverName = config.getString("mysql.driver", Constants.DEFAULT_DRIVER_NAME);
        
        Driver driver = SQLUtil.getDriver(driverName, getServer().getLogger());
        
        String url = config.getString("mysql.url", Constants.DEFAULT_URL);
        String username = config.getString("mysql.username");
        String password = config.getString("mysql.password");
        String primaryTableName = config.getString("mysql.table-name", Constants.DEFAULT_PRIMARY_TABLE_NAME);
        
        // Check table name
        if (!primaryTableName.matches("^[A-Za-z_]*$"))
            primaryTableName = Constants.DEFAULT_PRIMARY_TABLE_NAME;
        
        SuperSpawnManager manager = new SuperSpawnManager(getServer(), primaryTableName, url, username, password, driver);
        
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        Permission perms = rsp.getProvider();
        
        getCommand("superspawn").setExecutor(
                new SuperSpawnCommand(getServer(), 
                manager,
                C.c(getConfig().getString("messages.sender-not-player", "&cYou must be a player to execute this command!"))));
        
        getCommand("spawn").setExecutor(
                new SpawnCommand(
                        getServer(),
                        manager, 
                        perms,
                        C.c(getConfig().getString("messages.must-be-player", "&cYou must be a player to execute this command!")),
                        C.c(getConfig().getString("messages.no-such-player", "&cThe player {0} does not exist!")),
                        C.c(getConfig().getString("messages.no-perms", "&cInsufficient permissions to do /spawn player!"))));
        
        getServer().getPluginManager().registerEvents(new PlayerSpawnedListener(manager, perms), this);
    }
}
