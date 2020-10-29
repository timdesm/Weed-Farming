package com.knovosky.weedfarming.utils;

import com.knovosky.weedfarming.database.MySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpgradeUtil {

    public static double[] prices = {0.00, 325000.00, 9000000.00, 32000000.00, 90000000.00, 220000000.00, 550000000.00, 1200000000.00, 4000000000.00, 10000000000.00};
    private static int[] growingSeconds  = {300, 280, 260, 240, 220, 200, 180, 160, 140, 120};
    private static int[] farmUsage = {15, 16, 17, 18, 19, 20, 21, 22, 23, 24};

    public static Integer maxUpgrade = 10;

    public static int growingSeconds(int currUpgrade) {
        return growingSeconds[currUpgrade - 1];
    }

    public static int getFarmUsage(int currUpgrade) {
        return farmUsage[currUpgrade - 1];
    }

    public static boolean canBuy(MySQL database, String serverID, String userID) {
        int currUpgrade = getUpgrade(database, serverID, userID);
        if(currUpgrade < maxUpgrade) {
            if(prices[currUpgrade] <= EconomyUtil.getBalance(database, serverID, userID)) {
                return true;
            }
        }
        return false;
    }

    public static Integer getUpgrade(MySQL database, String serverID, String userID) {
        try {

            PreparedStatement ps = database.connection.prepareStatement("SELECT * FROM `user_upgrades` WHERE `server_id`=? AND `user_id`=?");
            ps.setString(1, serverID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt("upgrade");
            }
            ps.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
        return 1;
    }

    public static void setUpgrade(MySQL database, String serverID, String userID, Integer upgrade) {

        try {
            PreparedStatement ps = database.connection.prepareStatement("SELECT * FROM `user_upgrades` WHERE `server_id`=? AND `user_id`=?");
            ps.setString(1, serverID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                PreparedStatement ps2 = database.connection.prepareStatement("UPDATE `user_upgrades` SET `upgrade`=? WHERE `server_id`=? AND `user_id`=?");
                ps2.setInt(1, upgrade);
                ps2.setString(2, serverID);
                ps2.setString(3, userID);
                ps2.executeUpdate();
                ps2.close();
            }
            else {
                PreparedStatement ps2 = database.connection.prepareStatement("INSERT INTO `user_upgrades` (`server_id`, `user_id`, `upgrade`) VALUES ('" + serverID + "', '" + userID + "', upgrade);");
                ps2.executeUpdate();
                ps2.close();
            }
            ps.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }
}