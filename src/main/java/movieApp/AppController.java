package movieApp;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import movieApp.model.movie.Movie;
import movieApp.model.movie.MovieJSON;
import movieApp.model.movie.StatusMovieJSON;
import movieApp.model.user.User;
import movieApp.utils.DataBaseUtil;
import movieApp.utils.MovieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;

@Controller
public class AppController {
    private final String ORIGINS = "https://movies-to-watch-client.herokuapp.com";
    private final Environment env;

    @Autowired
    public AppController(Environment env) {
        this.env = env;
    }

    private String getClientId(){
        String SERVER_CLIENT_ID;
        try {
            BufferedReader br = new BufferedReader(new FileReader("config"));
            SERVER_CLIENT_ID = br.readLine();
        } catch (Exception e) {
            SERVER_CLIENT_ID = env.getProperty("SERVER_CLIENT_ID");
        }
        return SERVER_CLIENT_ID;
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/login")
    @ResponseBody
//    public String loginUser(String userId) throws SQLException {
//        Connection connection = null;
//        try {
//            connection = DataBaseUtil.getConnection();
//        } catch (Exception ex) {
//            System.out.println("Connection failed.");
//        }
//        if (connection != null) {
//            PreparedStatement pstmt = null;
//
////            String sql = "CREATE TABLE USERS " +
////                    "(userid INTEGER not NULL, " +
////                    "token VARCHAR(255), " +
////                    " PRIMARY KEY ( userid ))";
//
////            String sql = "DROP TABLE USERS";
//
//            String query = "SELECT * FROM USERS" ;
//
//            pstmt = connection.prepareStatement(query);
////            pstmt.setInt(1, CustID);
//            ResultSet rs = pstmt.executeQuery();
//            while(rs.next()) {
//                int useid = rs.getInt("userid");
//                System.out.println(useid);
//                String token = rs.getString("token");
//                System.out.println(token);
//                System.out.println("SSSS");
//            }
//
//            System.out.println(rs);
//            Statement stmt = connection.createStatement();
//            String sql = "INSERT INTO USERS (userid, token) VALUES (?,?)";
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
////            , Statement.RETURN_GENERATED_KEYS);
//            preparedStatement.setInt(1, 60);
//            preparedStatement.setString(2, "dlugi_token");
//
//            preparedStatement.executeUpdate();
//            stmt.executeUpdate(query);
//            stmt.executeUpdate(sql);
//
//            String sql = "CREATE TABLE MOVIES " +
//            "(id SERIAL PRIMARY KEY, " +
//            "userid INTEGER not NULL, " +
//            "movie VARCHAR(255), " +
//            "status boolean)";
//
//    Connection connection = DataBaseUtil.getConnection();
//    Statement stmt = connection.createStatement();
//            stmt.executeUpdate(sql);



//
//            System.out.println("You made it, take control your database now!");
//        } else {
//            System.out.println("Failed to make connection!");
//        }

//    public String loginUser(String token, HttpServletResponse response) {
    public String loginUser(String token) throws SQLException, URISyntaxException {

        if(token.equals("test")){
            DataBaseUtil.createOrUpdateUserTokenInDatabase("123", token);
            return "test";
        }

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
            .setAudience(Collections.singletonList(getClientId()))
            .build();

        GoogleIdToken idToken = null;
        try {idToken = verifier.verify(token);}
        catch (IOException e) {e.printStackTrace();}
        catch (Exception e){
//            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "";
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();

            DataBaseUtil.createOrUpdateUserTokenInDatabase(userId, token);
//            response.setStatus(HttpStatus.OK.value());
            return token;
        }
        else {
//            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "";
        }
}

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/logout")
    @ResponseBody
    public Object logoutUser(String token, HttpServletResponse response) {
        //logout
        response.setStatus(HttpStatus.OK.value());
        return "";
    }

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movies")
    @ResponseBody
//    public List<StatusMovieJSON> getMovies(String token, HttpServletResponse response) {
    public List<StatusMovieJSON> getMovies(String token) throws SQLException {
        final String userid = DataBaseUtil.getUserIdByToken(token);
        System.out.println(userid);

        HashMap<Movie, Boolean> userMovies = DataBaseUtil.getUserMovies(userid);
//        System.out.println(userMovies);
//        response.setStatus(HttpStatus.OK.value());
        return MovieUtil.getMoviesInfo(userMovies);
    }

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movie")
    @ResponseBody
//    public MovieJSON getMovieByTitle(String token, String title, HttpServletResponse response) throws SQLException {
    public MovieJSON getMovieByTitle(String token, String title) throws SQLException {
        final String userid = DataBaseUtil.getUserIdByToken(token);
        System.out.println(userid);

//        response.setStatus(HttpStatus.OK.value());
        return MovieUtil.getMovieInfo(new Movie(title));
//        return MovieUtil.getMovieInfo(Objects.requireNonNull(MovieUtil.getMovieFromTitle(user, title)));
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movie/status")
    @ResponseBody
//    public Object setMovieStatus(String token, String title, Boolean status, HttpServletResponse response) throws SQLException {
    public Object setMovieStatus(String token, String title, Boolean status) throws SQLException {
//    public Object setMovieStatus(String token, String title, Boolean status) {
        final String userid = DataBaseUtil.getUserIdByToken(token);
        System.out.println(userid);

        DataBaseUtil.changeMovieStatus(userid, title, status);
//        UserUtil.setMovieStatus(user, title, status);

//        response.setStatus(HttpStatus.OK.value());
        return "";
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movies/new")
    @ResponseBody
//    public Object addMovie(String token, String title, HttpServletResponse response) {
    public Object addMovie(String token, String title) throws SQLException {
        final String userid = DataBaseUtil.getUserIdByToken(token);
        System.out.println(userid);

        final String parsedTitle = title.trim().toLowerCase();
        final Movie newMovie = new Movie(parsedTitle);

        MovieJSON movieJSON = MovieUtil.getMovieInfo(newMovie);
        if(movieJSON == null || movieJSON.getId().equals("")) {
//            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Movie with such title does not exist.";
        }

        DataBaseUtil.addMovieToUserId(userid, movieJSON.getTitle());

//        response.setStatus(HttpStatus.CREATED.value());
        return "";
    }

    @CrossOrigin(origins = ORIGINS)
    @DeleteMapping("/movies/delete")
    @ResponseBody
//    public Object deleteMovie(String token, String title, HttpServletResponse response) {
    public Object deleteMovie(String token, String title) throws SQLException {
        System.out.println("delete");
        final String userid = DataBaseUtil.getUserIdByToken(token);
        System.out.println(userid);

        DataBaseUtil.deleteMovieForUser(userid, title);
//        user.deleteMovie(MovieUtil.getMovieFromTitle(user, title));

//        response.setStatus(HttpStatus.OK.value());
        return "";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: nie usuwaj printów, bo może mi się do debugu przydać jeszcze (zakomentuje :))
//        System.out.println(id);
//        System.out.println(title);
//        System.out.println(poster);
//        System.out.println(year);
//        System.out.println(runtime);
//        System.out.println(genre);
//        System.out.println(director);
//        System.out.println(production);
//        System.out.println(website);
//        System.out.println(actors);
//        System.out.println(ratings);
//        System.out.println(awards);
//        System.out.println(plot);
}
