package com.example.coin_panion.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Line {
    static String host = "mysql-102814-0.cloudclusters.net";
    static String port = "10177";
    static String user = "nazrin";
    static String password = "nazrin0965";
    static String database = "coinpanion";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&useSSL=false", host, port, database, user, password));
    }
}
