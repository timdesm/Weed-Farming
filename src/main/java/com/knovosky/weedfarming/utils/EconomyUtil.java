package com.knovosky.weedfarming.utils;

import com.knovosky.weedfarming.database.MySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class EconomyUtil {

    public static void setBalance(MySQL database, String serverID, String userID, double balance) {
        try {
            PreparedStatement ps = database.connection.prepareStatement("UPDATE `user_balance` SET `balance`=? WHERE `server_id`=? AND `user_id`=?");
            ps.setDouble(1, balance);
            ps.setString(2, serverID);
            ps.setString(3, userID);
            ps.executeUpdate();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    public static double getBalance(MySQL database, String serverID, String userID) {
        try {
            PreparedStatement ps =   database.connection.prepareStatement("SELECT * FROM `user_balance` WHERE `server_id`=? AND `user_id`=?");
            ps.setString(1, serverID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getDouble("balance");
            }
        }
        catch (SQLException err) {
            System.out.println(err.getMessage());
        }

        return 0.0;
    }

    public static String formatNumber(double number) {
        DecimalFormat df2 = new DecimalFormat("#.##");
        return df2.format(number);
    }
}
