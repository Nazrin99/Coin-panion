package com.example.coin_panion.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Line {
    String conString, host, port, database, user, password;

    public Line(String host, String port, String database, String user, String password) {
        this.conString = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public Connection getLine(){
        try{
            return DriverManager.getConnection(conString, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeDatabase(String database){
        this.database = database;
        this.conString = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
    }

    public String getConString() {
        return conString;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
