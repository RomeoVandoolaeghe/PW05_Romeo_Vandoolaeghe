package fr.isen.java2.db.daos;

import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

import java.sql.*;
import java.util.ArrayList;

public class MovieDao {

	public List<Movie> listMovies() {
		List<Movie> movies = new ArrayList<>();
		String sql = "SELECT movie.idmovie AS movie_id, movie.title, movie.release_date, movie.duration, movie.director, movie.summary, genre.idgenre AS genre_id, genre.name AS genre_name "
				+ "FROM movie "
				+ "JOIN genre ON movie.genre_id = genre.idgenre";

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				movies.add(new Movie(
						resultSet.getInt("movie_id"),
						resultSet.getString("title"),
						resultSet.getDate("release_date").toLocalDate(),
						new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name")),
						resultSet.getInt("duration"),
						resultSet.getString("director"),
						resultSet.getString("summary")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movies;
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> movies = new ArrayList<>();
		String sql = "SELECT movie.idmovie AS movie_id, movie.title, movie.release_date, movie.duration, movie.director, movie.summary, genre.idgenre AS genre_id, genre.name AS genre_name "
				+ "FROM movie "
				+ "JOIN genre ON movie.genre_id = genre.idgenre "
				+ "WHERE genre.name = ?";

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, genreName);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					movies.add(new Movie(
							resultSet.getInt("movie_id"),
							resultSet.getString("title"),
							resultSet.getDate("release_date").toLocalDate(),
							new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name")),
							resultSet.getInt("duration"),
							resultSet.getString("director"),
							resultSet.getString("summary")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movies;
	}

	public Movie addMovie(Movie movie) {
		String sql = "INSERT INTO movie(title, release_date, genre_id, duration, director, summary) VALUES(?,?,?,?,?,?)";

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setString(1, movie.getTitle());
			statement.setDate(2, Date.valueOf(movie.getReleaseDate()));
			statement.setInt(3, movie.getGenre().getId());
			statement.setInt(4, movie.getDuration());
			statement.setString(5, movie.getDirector());
			statement.setString(6, movie.getSummary());
			statement.executeUpdate();

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					movie.setId(generatedKeys.getInt(1));
					return movie;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
