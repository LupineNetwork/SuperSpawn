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
package com.lupinenetwork.superspawn.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Location;
import org.bukkit.Server;

/**
 *
 * @author Majora320 &lt;Majora320@gmail.com&gt;
 */
public class SuperSpawnManager {
    private final Server server;
    private final Driver driver;
    private final String url;
    private final String username;
    private final String password;
    private final String primaryTableName;
    
    public SuperSpawnManager(Server server, String primaryTableName, String url, String username, String password, Driver driver) {
        this.driver = driver;
        this.server = server;
        this.url = url;
        this.username = username;
        this.password = password;
        this.primaryTableName = primaryTableName;
    }
    
    /**
     * Do the boilerplate required to setup tables, etc.
     *
     * @param conn the connection to operate on
     * @throws SQLException if there is an error with the database
     */
    protected final void initializeDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + primaryTableName + "(id BIGINT NOT NULL AUTO_INCREMENT, group_name VARCHAR(255), world_name VARCHAR(255), x REAL, y REAL, z REAL, pitch FLOAT, yaw FLOAT, PRIMARY KEY(id))");
        }
    }
    
    protected final Connection openConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password);
        initializeDatabase(conn);
        return conn;
    }
    
    protected Location toLocation(ResultSet rs) throws SQLException {
        return new Location(server.getWorld(rs.getString("world_name")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("pitch"), rs.getFloat("yaw"));
    }
    
    public void addGroupSpawn(String group, Location loc) throws SuperSpawnDatabaseException {
        deleteGroupSpawn(group, loc.getWorld().getName());
        try (Connection conn = openConnection();
                PreparedStatement insert = conn.prepareStatement("INSERT INTO " + primaryTableName + " (group_name, world_name, x, y, z, pich, yaw) VALUES (?, ?, ?, ?, ?)")) {
            insert.setString(1, group);
            insert.setString(2, loc.getWorld().getName());
            insert.setDouble(3, loc.getX());
            insert.setDouble(4, loc.getY());
            insert.setDouble(5, loc.getZ());
            insert.setFloat(6, loc.getPitch());
            insert.setFloat(7, loc.getYaw());
            insert.execute();
        } catch (SQLException ex) {
            throw new SuperSpawnDatabaseException(ex);
        }
    }
    
    public Location getGroupSpawn(String group, String world) throws SuperSpawnDatabaseException {
        try (Connection conn = openConnection();
                PreparedStatement fetch = conn.prepareStatement("SELECT * FROM " + primaryTableName + " WHERE (group_name = ?) AND (world_name = ?)")) {
            fetch.setString(1, group);
            fetch.setString(2, world);
            ResultSet results = fetch.executeQuery();
            
            if (results.next())
                return toLocation(results);
            else
                return getDefaultSpawn();
        } catch (SQLException ex) {
            throw new SuperSpawnDatabaseException(ex);
        }
    }
    
    public Location getDefaultSpawn() throws SuperSpawnDatabaseException {
        try (Connection conn = openConnection();
                PreparedStatement fetch = conn.prepareStatement("SELECT * FROM " + primaryTableName + " WHERE (group_name = ?)")) {
            fetch.setString(1, "*");
            ResultSet results = fetch.executeQuery();
            
            if (results.next())
                return toLocation(results);
            else
                return null;
        } catch (SQLException ex) {
            throw new SuperSpawnDatabaseException(ex);
        }
    }
    
    public void deleteGroupSpawn(String group, String world) throws SuperSpawnDatabaseException {
        try (Connection conn = openConnection();
                PreparedStatement delete = conn.prepareStatement("DELETE FROM " + primaryTableName + " WHERE (group_name = ?) AND (world_name = ?)")) {
            delete.setString(1, group);
            delete.setString(2, world);
            delete.execute();
        } catch (SQLException ex) {
            throw new SuperSpawnDatabaseException(ex);
        }
    }
}
