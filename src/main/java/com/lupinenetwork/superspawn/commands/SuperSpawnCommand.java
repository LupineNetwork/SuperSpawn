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
package com.lupinenetwork.superspawn.commands;

import com.lupinenetwork.superspawn.database.SuperSpawnDatabaseException;
import com.lupinenetwork.superspawn.database.SuperSpawnManager;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Majora320 &lt;Majora320@gmail.com&gt;
 */
public class SuperSpawnCommand implements CommandExecutor {
    private final Server server;
    private final SuperSpawnManager manager;
    private final String senderNotPlayerMsg;
    
    public SuperSpawnCommand(Server server, SuperSpawnManager manager, String senderNotPlayerMsg) {
        this.server = server;
        this.manager = manager;
        this.senderNotPlayerMsg = senderNotPlayerMsg;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String group;
        
        if (args.length < 1)
            return false;
        
        group = args[0];
        String world;
        double x, y, z;
        boolean havePitchAndYaw = false;
        float pitch = 0, yaw = 0;
        
        if ((args.length > 1 && args.length < 4) || args.length > 5)
            return false;
        
        if (group.equals("*")) {
            if (args.length >= 2) {
                world = args[1];
            } else {
                if (sender instanceof Player) {
                    world = ((Player)sender).getWorld().getName();
                } else {
                    sender.sendMessage(senderNotPlayerMsg);
                    return true;
                }                
            }
            
            if (args.length == 5) {
                try {
                    x = Float.parseFloat(args[1]);
                    y = Float.parseFloat(args[2]);
                    z = Float.parseFloat(args[3]);
                } catch (NumberFormatException ex) {
                    return false;
                }
            } else {
                if (sender instanceof Player) {
                    Location loc = ((Player)sender).getLocation();

                    x = loc.getX();
                    y = loc.getY();
                    z = loc.getZ();

                    havePitchAndYaw = true;
                    pitch = loc.getPitch();
                    yaw = loc.getYaw();
                } else {
                    sender.sendMessage(senderNotPlayerMsg);
                    return true;
                }
            }
        } else {
            if (args.length >= 4) {
                try {
                    x = Float.parseFloat(args[1]);
                    y = Float.parseFloat(args[2]);
                    z = Float.parseFloat(args[3]);
                } catch (NumberFormatException ex) {
                    return false;
                }
            } else {
                if (sender instanceof Player) {
                    Location loc = ((Player)sender).getLocation();

                    x = loc.getX();
                    y = loc.getY();
                    z = loc.getZ();

                    havePitchAndYaw = true;
                    pitch = loc.getPitch();
                    yaw = loc.getYaw();
                } else {
                    sender.sendMessage(senderNotPlayerMsg);
                    return true;
                }
            }
        
            if (args.length == 5)
                world = args[4];
            else {
                if (sender instanceof Player) {
                    world = ((Player)sender).getWorld().getName();
                } else {
                    sender.sendMessage(senderNotPlayerMsg);
                    return true;
                }
            }    
        }
        
        try {
            if (havePitchAndYaw)
                manager.addGroupSpawn(group, new Location(server.getWorld(world), x, y, z, pitch, yaw));
            else
                manager.addGroupSpawn(group, new Location(server.getWorld(world), x, y, z));
        } catch (SuperSpawnDatabaseException ex) {
            throw new RuntimeException(ex);
        }
        
        return true;
    }
   
}
