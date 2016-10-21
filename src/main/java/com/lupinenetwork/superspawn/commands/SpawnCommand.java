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
import java.text.MessageFormat;
import net.milkbowl.vault.permission.Permission;
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
public class SpawnCommand implements CommandExecutor {
    private final Server server;
    private final SuperSpawnManager manager;
    private final Permission perms;
    private final String senderNotPlayerMsg;
    private final String noSuchPlayerMsg;
    private final String noPermissionsMsg;
    
    public SpawnCommand(Server server, SuperSpawnManager manager, Permission perms, String senderNotPlayerMsg, String noSuchPlayerMsg, String noPermissionsMsg) {
        this.server = server;
        this.manager = manager;
        this.perms = perms;
        this.senderNotPlayerMsg = senderNotPlayerMsg;
        this.noSuchPlayerMsg = noSuchPlayerMsg;
        this.noPermissionsMsg = noPermissionsMsg;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        
        if (args.length < 1) {
            if (!(sender instanceof Player))
                sender.sendMessage(senderNotPlayerMsg);

            player = (Player)sender;
        } else {
            if (!sender.hasPermission("superspawn.sendplayertospawn")) {
                sender.sendMessage(noPermissionsMsg);
                return true;
            }
            player = server.getPlayer(args[0]);
            
            if (player == null) {
                sender.sendMessage(MessageFormat.format(noSuchPlayerMsg, args[0]));
                return true;
            }
        }

        try {
            Location spawn = manager.getGroupSpawn(perms.getPrimaryGroup(player), player.getWorld().getName());
            if (spawn == null) return true;
            player.teleport(spawn);
        } catch (SuperSpawnDatabaseException ex) {
            throw new RuntimeException(ex);
        }
        
        return true;
    }
}
