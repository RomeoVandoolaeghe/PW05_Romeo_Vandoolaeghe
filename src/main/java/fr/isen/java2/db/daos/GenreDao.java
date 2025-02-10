package fr.isen.java2.db.daos;

import fr.isen.java2.db.entities.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDao {

    public List<Genre> listGenres() {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM genre";

        try (Connection connection = DataSourceFactory.getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                genres.add(new Genre(
                    resultSet.getInt("idgenre"),
                    resultSet.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return genres;
    }


    public Genre getGenre(String name) {
        String sql = "SELECT * FROM genre WHERE name = ?";
        try (Connection connection = DataSourceFactory.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Genre(
                        resultSet.getInt("idgenre"),
                        resultSet.getString("name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; //si pas de genre
    }


    public void addGenre(String name) {
        String sql = "INSERT INTO genre(name) VALUES(?)";

        try (Connection connection = DataSourceFactory.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
