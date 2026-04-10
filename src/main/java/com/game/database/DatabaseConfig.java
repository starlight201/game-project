package com.game.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    // 数据库配置
    private static final String URL = "jdbc:mysql://192.168.10.100:3306/redpacket_game";
    private static final String USER = "root";
    private static final String PASSWORD = "Wcy200523.";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}