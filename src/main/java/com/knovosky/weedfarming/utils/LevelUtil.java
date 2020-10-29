package com.knovosky.weedfarming.utils;

import com.knovosky.weedfarming.database.MySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LevelUtil {

    public static double price = 1325.00;
    public static Integer maxLevel = 10;

    public static Double getPrice(Integer level) {
        return price * (level - 1);
    }

    public static Integer getLevel(MySQL database, String serverID, String userID) {
        try {

            PreparedStatement ps = database.connection.prepareStatement("SELECT * FROM `user_level` WHERE `server_id`=? AND `user_id`=?");
            ps.setString(1, serverID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt("level");
            }
            ps.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
        return 0;
    }

    public static void setLevel(MySQL database, String serverID, String userID, Integer level) {
        try {
            PreparedStatement ps = database.connection.prepareStatement("UPDATE `user_level` SET `level`=? WHERE `server_id`=? AND `user_id`=?");
            ps.setInt(1, level);
            ps.setString(2, serverID);
            ps.setString(3, userID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }
}
