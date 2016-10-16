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
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Majora320 &lt;Majora320@gmail.com&gt;
 */
public class SpawnCommand implements CommandExecutor {
    private final SuperSpawnManager manager;
    private final Permission perms;
    private final String senderNotPlayerMsg;
    
    public SpawnCommand(SuperSpawnManager manager, Permission perms, String senderNotPlayerMsg) {
        this.manager = manager;
        this.perms = perms;
        this.senderNotPlayerMsg = senderNotPlayerMsg;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            sender.sendMessage(senderNotPlayerMsg);
        
        Player player = (Player)sender;
        
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
