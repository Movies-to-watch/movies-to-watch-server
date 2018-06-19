package movieApp.utils;

import movieApp.model.movie.Movie;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.HashMap;
import java.util.Objects;

public final class DataBaseUtil {

    public static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI("postgres://oheawsscocggss:2641e19d7d1a9b48d804b24f8631a6558ddc5d1a98caf7a54049ef1cd2edff81@ec2-54-235-206-118.compute-1.amazonaws.com:5432/d744qe7326f8in");
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
        return DriverManager.getConnection(dbUrl, username, password);
    }

    public static String getUserIdByToken(String token) throws SQLException {
        Connection connection = null;
        ResultSet result;
        try {
            connection = getConnection();
        } catch (Exception ex) {
            System.out.println("Connection failed.");
        }
        if (connection != null) {
            String query = "SELECT userid FROM USERS WHERE token=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, token);
            result = pstmt.executeQuery();
            Objects.requireNonNull(connection).close();

            if(result.next()) {
                int userid = result.getInt("userid");
                return String.valueOf(userid);
            }
        }
        return "";
    }

    public static void createOrUpdateUserTokenInDatabase(String userid, String newToken) throws SQLException {
        Connection connection = null;
        ResultSet result;
        try {
            connection = getConnection();
        } catch (Exception ex) {
            System.out.println("Connection failed.");
        }
        if (connection != null) {
            String query = "SELECT * FROM USERS WHERE userid=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(userid));
            result = pstmt.executeQuery();

            if (result.next()) {
                String sql = "UPDATE USERS SET token = ? WHERE userid = ?";
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, newToken);
                pstmt.setInt(2, Integer.parseInt(userid));
            } else {
                String sql = "INSERT INTO USERS (userid, token) VALUES (?,?)";
                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(userid));
                pstmt.setString(2, newToken);
            }
            pstmt.executeUpdate();
            Objects.requireNonNull(connection).close();
        }
    }

    public static HashMap<Movie, Boolean> getUserMovies(String userid) throws SQLException {
        Connection connection = null;
        ResultSet result;
        try {
            connection = getConnection();
        } catch (Exception ex) {
            System.out.println("Connection failed.");
        }
        if (connection != null) {
            String query = "SELECT * FROM MOVIES WHERE userid=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(userid));
            result = pstmt.executeQuery();

            final HashMap<Movie, Boolean> userMovies = new HashMap<>();
            while (result.next()) {
                String movieTitle = result.getString("movie");
                Boolean status = result.getBoolean("status");
                userMovies.put(new Movie(movieTitle), status);
                Objects.requireNonNull(connection).close();
            }
            return userMovies;
        }
        return new HashMap<>();
    }

    public static void addMovieToUserId(String userid, String title) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (Exception ex) {
            System.out.println("Connection failed.");
        }
        if (connection != null) {
            String sql = "INSERT INTO MOVIES (userid, movie, status) VALUES (?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(userid));
            pstmt.setString(2, title);
            pstmt.setBoolean(3, Boolean.FALSE);
            pstmt.executeUpdate();
        }
        Objects.requireNonNull(connection).close();
    }

    public static boolean checkIfMovieWasAlreadyAdded(String userid, String title) throws SQLException {
        Connection connection = null;
        ResultSet result;
        try {
            connection = getConnection();
        } catch (Exception ex) {
            System.out.println("Connection failed.");
        }
        if (connection != null) {
            String query = "SELECT * FROM MOVIES WHERE userid=? AND movie=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(userid));
            pstmt.setString(2, title);
            result = pstmt.executeQuery();
            Objects.requireNonNull(connection).close();
            return result.next();
        }
        return false;
    }

    public static void changeMovieStatus(String userid, String title, Boolean status) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (Exception ex) {
            System.out.println("Connection failed.");
        }
        if (connection != null) {
            String query = "UPDATE MOVIES SET status = ? WHERE userid = ? AND movie=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setBoolean(1, status);
            pstmt.setInt(2, Integer.parseInt(userid));
            pstmt.setString(3, title);
            pstmt.executeUpdate();
        }
        Objects.requireNonNull(connection).close();
    }

    public static void deleteMovieForUser(String userid, String title) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (Exception ex) {
            System.out.println("Connection failed.");
        }
        if (connection != null) {
            String query = "DELETE FROM MOVIES WHERE userid = ? AND movie=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(userid));
            pstmt.setString(2, title);
            pstmt.executeUpdate();
        }
        Objects.requireNonNull(connection).close();
    }
}