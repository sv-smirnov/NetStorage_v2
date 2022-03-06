package com.example.netstorage_v2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServerAuth {
    private Connection connection;
    private Statement statement;

    private class User {
        private String login;
        private String pass;

        public User(String login, String pass) {
            this.login = login;
            this.pass = pass;
        }
    }

    private List<User> users;

    public void start() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        statement = connection.createStatement();
        users = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery("SELECT * FROM users;")) {
            while (rs.next()) {
                users.add(new User(rs.getString("login"), rs.getString("password")));
            }
        }
    }

    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authCheck(String login, String pass) {
        for (User o : users) {
            if (o.login.equals(login) && o.pass.equals(pass)) return true;
        }
        return false;
    }

    public boolean registration(String login, String password) throws SQLException {
        for (User o : users) {
            if (o.login.equals(login)) return false;
        }
        PreparedStatement ps = connection.prepareStatement("INSERT INTO users (login, password) VALUES (?, ?)");
        ps.setString(1, login);
        ps.setString(2, password);
        int rs = ps.executeUpdate();
        this.start();
        return true;
    }

}


