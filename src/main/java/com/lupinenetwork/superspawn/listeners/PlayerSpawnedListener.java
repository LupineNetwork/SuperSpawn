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
package com.lupinenetwork.superspawn.listeners;

import com.lupinenetwork.superspawn.database.SuperSpawnDatabaseException;
import com.lupinenetwork.superspawn.database.SuperSpawnManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Location;
abstract 
/**
 *
 * @author Majora320 &lt;Majora320@gmail.com&gt;
 */
public class PlayerSpawnedListener implements Listener {
    private final SuperSpawnManager manager;
    private final Permission perms;
    
    public PlayerSpawnedListener(SuperSpawnManager manager, Permission perms) {
        this.manager = manager;
        this.perms = perms;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        try {
            Location spawn = manager.getGroupSpawn(perms.getPrimaryGroup(evt.getPlayer()), evt.getPlayer().getWorld().getName());
            if (spawn == null) return;
            evt.getPlayer().teleport(spawn);
        } catch (SuperSpawnDatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangeWorld(PlayerMoveEvent evt) {
        if (evt.getFrom().getWorld().equals(evt.getTo().getWorld()))
            return;
        
        try {
            Location spawn = manager.getGroupSpawn(perms.getPrimaryGroup(evt.getPlayer()), evt.getTo().getWorld().getName());
            if (spawn == null) return;
            evt.getPlayer().teleport(spawn);
        } catch (SuperSpawnDatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
