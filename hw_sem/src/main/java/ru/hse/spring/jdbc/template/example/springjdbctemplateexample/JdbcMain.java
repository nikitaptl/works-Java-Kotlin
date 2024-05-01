package ru.hse.spring.jdbc.template.example.springjdbctemplateexample;

import java.sql.*;
import java.util.Properties;

public class JdbcMain {

    public static void render(Person person) {
        System.out.println(person);
    }

    public static void main(String[] args) throws SQLException {
        Properties connectionProperties = new Properties();

        connectionProperties.setProperty("user", "postgres");
        connectionProperties.setProperty("password", "123456");

        try {
            Class.forName("org.postgresql.Driver")
        }
        catch(ClassNotFoundException e) {
            System.out.println("Не найден JDBC драйвер для PostgreSQL.");
            e.printStackTrace();
            return;
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", connectionProperties);

            PreparedStatement preparedStatement = connection.prepareStatement("select first_name, last_name from person where id = ?");

            Person person = null;

            preparedStatement.setInt(1, 2);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                person = new Person(resultSet.getString("first_name"), resultSet.getString("last_name"));

                render(person);
            }
        }
        catch(SQLException e) {
            System.out.println("Возникла проблема с SQL:");
            e.printStackTrace();
            return;
        }
    }
}
