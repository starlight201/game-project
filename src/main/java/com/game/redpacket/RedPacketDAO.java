package com.game.redpacket;

import com.game.database.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 红包数据访问对象 - 用于数据库操作
 */
public class RedPacketDAO {
    /**
     * 更新用户总金额（在数据库中增加金额）
     * @param amount 要增加的金额
     */
    public static void updateTotalMoney(double amount) {
        String sql = "UPDATE user SET total_money = total_money + ? WHERE id = 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户总金额
     * @return 用户的总金额
     */
    public static double getTotalMoney() {
        String sql = "SELECT total_money FROM user WHERE id = 1";
        double totalMoney = 0.0;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                totalMoney = rs.getDouble("total_money");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalMoney;
    }
}