package com.knovosky.weedfarming.utils;

import com.knovosky.weedfarming.database.MySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;


public class FarmUtil {

    public static double price = 245.00;

    public static Double reward_min = 15.0;
    public static Double reward_max = 55.0;

    public static boolean hasBusiness(MySQL database, String serverID, String userID) {

        try {
            PreparedStatement ps = database.connection.prepareStatement("SELECT * FROM `user_balance` WHERE `server_id`=? AND `user_id`=?");
            ps.setString(1, serverID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }
            else {
                PreparedStatement ps1 = database.connection.prepareStatement("SELECT * FROM `user_balance` WHERE `server_id`=? AND `user_id`=?");
                ps1.setString(1, serverID);
                ps1.setString(2, userID);
                ResultSet rs1 = ps1.executeQuery();
                if(rs1.next()) {
                    return true;
                }
                ps1.close();
            }
            ps.close();
        }
        catch (Exception err) {
            System.out.println(err.getMessage());
        }

        return false;
    }

    public static void openBusiness(MySQL database, String serverID, String userID) {
        try {
            PreparedStatement ps = database.connection.prepareStatement("INSERT INTO `user_balance` (`server_id`, `user_id`, `balance`) VALUES ('" + serverID + "', '" + userID + "', '" + 600 +  "');");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }

        try {
            PreparedStatement ps1 = database.connection.prepareStatement("INSERT INTO `user_level` (`server_id`, `user_id`) VALUES ('" + serverID + "', '" + userID + "');");
            ps1.executeUpdate();
            ps1.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }

        try {
            PreparedStatement ps2 = database.connection.prepareStatement("INSERT INTO `user_upgrades` (`server_id`, `user_id`) VALUES ('" + serverID + "', '" + userID + "');");
            ps2.executeUpdate();
            ps2.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    public static void addPlant(MySQL database, String serverID, String userID, Long amount) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'GMT'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();

        try {
            PreparedStatement ps = database.connection.prepareStatement("INSERT INTO `user_plants` (`server_id`, `user_id`, `last_farmed`, `amount`) VALUES ('" + serverID + "', '" + userID + "', '" + dateFormat.format(date) + "', '" + amount + "');");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    public static List<Integer> farmablePlats(MySQL database, String serverID, String userID) {
        List<Integer> list  = new ArrayList<Integer>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'GMT'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();

        Integer delay = UpgradeUtil.growingSeconds(UpgradeUtil.getUpgrade(database, serverID, userID));

        try {
            PreparedStatement ps =   database.connection.prepareStatement("SELECT * FROM `user_plants` WHERE `server_id`=? AND `user_id`=? AND `dead`=0");
            ps.setString(1, serverID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String str_date = rs.getString("last_farmed");
                if(TimeUtil.stringToSeconds(str_date) + delay < TimeUtil.stringToSeconds(dateFormat.format(date))) {
                    list.add(rs.getInt("id"));
                }
            }
            ps.close();
        }
        catch (SQLException err) {
            System.out.println(err.getMessage());
        }

        return list;
    }

    public static void farmPlant(MySQL database, int plantID, int maxUsage) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'GMT'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date();

        try {
            PreparedStatement ps = database.connection.prepareStatement("UPDATE `user_plants` SET `last_farmed`=? WHERE `id`=? AND `dead`=0");
            ps.setString(1, dateFormat.format(date));
            ps.setInt(2, plantID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }

        try {
            PreparedStatement ps = database.connection.prepareStatement("SELECT * FROM `user_plants` WHERE `id`=? AND `dead`=0");
            ps.setInt(1, plantID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                Integer used = rs.getInt("used");
                used += 1;

                PreparedStatement ps1 = database.connection.prepareStatement("UPDATE `user_plants` SET `used`=? WHERE `id`=?");
                ps1.setInt(1, used);
                ps1.setInt(2, plantID);
                ps1.executeUpdate();
                ps1.close();


                if(used >= maxUsage) {
                    PreparedStatement ps2 = database.connection.prepareStatement("UPDATE `user_plants` SET `dead`=1 WHERE `id`=?");
                    ps2.setInt(1, plantID);
                    ps2.executeUpdate();
                    ps2.close();
                }
            }
            ps.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }


    public static Integer getUsage(MySQL database, Integer plantID) {
        try {

            PreparedStatement ps = database.connection.prepareStatement("SELECT * FROM `user_plants` WHERE `id`=?");
            ps.setInt(1, plantID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt("used");
            }
            ps.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
        return 0;
    }

    public static List<Integer> deadPlants(MySQL database, String serverID, String userID) {
        List<Integer> list  = new ArrayList<Integer>();

        try {
            PreparedStatement ps = database.connection.prepareStatement("SELECT * FROM `user_plants` WHERE `server_id`=? AND `user_id`=? AND `dead`=1");
            ps.setString(1, serverID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(rs.getInt("id"));
            }
            ps.close();
        }
        catch (SQLException err) {
            System.out.println(err.getMessage());
        }

        return list;
    }

    public static void checkDeadPlants(MySQL database, String serverID, String userID, int maxUsage) {
        try {
            PreparedStatement ps = database.connection.prepareStatement("SELECT * FROM `user_plants` WHERE `server_id`=? AND `user_id`=? AND `dead`=0");
            ps.setString(1, serverID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                if(rs.getInt("used") >= maxUsage) {
                    Integer id = rs.getInt("id");

                    PreparedStatement ps1 = database.connection.prepareStatement("UPDATE `user_plants` SET `dead`=1 WHERE `id`=?");
                    ps1.setInt(1, id);
                    ps1.executeUpdate();
                    ps1.close();
                }
            }
            ps.close();
        }
        catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    public static long totalPlants(MySQL database, String serverID, String userID) {
        Long amount = 0L;

        try {
            PreparedStatement ps =   database.connection.prepareStatement("SELECT * FROM `user_plants` WHERE `server_id`=? AND `user_id`=?");
            ps.setString(1, serverID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                amount += rs.getLong("amount");
            }
            ps.close();
        }
        catch (SQLException err) {
            System.out.println(err.getMessage());
        }

        return amount;
    }

    public static long totalDead(MySQL database, String serverID, String userID) {
        Long amount = 0L;
        for(Integer id : deadPlants(database, serverID, userID)) {
            amount += getAmount(database, id);
        }
        return amount;
    }

    public static long totalFarmable(MySQL database, String serverID, String userID) {
        Long amount = 0L;
        for(Integer id : farmablePlats(database, serverID, userID)) {
            amount += getAmount(database, id);
        }
        return amount;
    }

    public static long getAmount(MySQL database, Integer plantID) {
        try {
            PreparedStatement ps =   database.connection.prepareStatement("SELECT * FROM `user_plants` WHERE `id`=?");
            ps.setInt(1, plantID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getLong("amount");
            }
            ps.close();
        }
        catch (SQLException err) {
            System.out.println(err.getMessage());
        }
        return 0;
    }

    public static Double getReward() {
        double random = ThreadLocalRandom.current().nextDouble(reward_min, reward_max);
        return random;
    }

    public static int getUpgrade(MySQL database, String serverID, String userID) {
        int upgrade = 1;

        try {
            PreparedStatement ps =   database.connection.prepareStatement("SELECT * FROM `user_upgrades` WHERE `server_id`=? AND `user_id`=?");
            ps.setString(1, serverID);
            ps.setString(2, userID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                upgrade = rs.getInt("upgrade");
            }
            ps.close();
        }
        catch (SQLException err) {
            System.out.println(err.getMessage());
        }

        return upgrade;
    }
}
