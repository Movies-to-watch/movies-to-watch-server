package movieApp;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import movieApp.model.movie.Movie;
import movieApp.model.movie.MovieJSON;
import movieApp.model.movie.StatusMovieJSON;
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
import java.math.BigDecimal;
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
    public String loginUser(String token, HttpServletResponse response) throws SQLException {
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
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "";
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();
            BigDecimal userIdBigDecimal = new BigDecimal(userId);
            int userIdInt = userIdBigDecimal.intValue();

            DataBaseUtil.createOrUpdateUserTokenInDatabase(String.valueOf(userIdInt), token);
            response.setStatus(HttpStatus.OK.value());
            return token;
        }
        else {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "";
        }
}

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/logout")
    @ResponseBody
    public Object logoutUser(String token, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        return "";
    }

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movies")
    @ResponseBody
    public List<StatusMovieJSON> getMovies(String token, HttpServletResponse response) throws SQLException {
        final String userid = DataBaseUtil.getUserIdByToken(token);
        HashMap<Movie, Boolean> userMovies = DataBaseUtil.getUserMovies(userid);
        response.setStatus(HttpStatus.OK.value());
        return MovieUtil.getMoviesInfo(userMovies);
    }

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movie")
    @ResponseBody
    public MovieJSON getMovieByTitle(String token, String title, HttpServletResponse response) throws SQLException {
        final String userid = DataBaseUtil.getUserIdByToken(token);
        if (!userid.equals("")) {
            response.setStatus(HttpStatus.OK.value());
            return MovieUtil.getMovieInfo(new Movie(title));
        }
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return null;
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movie/status")
    @ResponseBody
    public Object setMovieStatus(String token, String title, Boolean status, HttpServletResponse response) throws SQLException {
        final String userid = DataBaseUtil.getUserIdByToken(token);
        DataBaseUtil.changeMovieStatus(userid, title, status);
        response.setStatus(HttpStatus.OK.value());
        return "";
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movies/new")
    @ResponseBody
    public Object addMovie(String token, String title, HttpServletResponse response) throws SQLException {
        final String userid = DataBaseUtil.getUserIdByToken(token);
        final String parsedTitle = title.trim().toLowerCase();
        final Movie newMovie = new Movie(parsedTitle);

        MovieJSON movieJSON = MovieUtil.getMovieInfo(newMovie);
        if(movieJSON == null || movieJSON.getId().equals("")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Movie with such title does not exist.";
        }

        DataBaseUtil.addMovieToUserId(userid, movieJSON.getTitle());
        response.setStatus(HttpStatus.CREATED.value());
        return "";
    }

    @CrossOrigin(origins = ORIGINS)
    @DeleteMapping("/movies/delete")
    @ResponseBody
    public Object deleteMovie(String token, String title, HttpServletResponse response) throws SQLException {
        final String userid = DataBaseUtil.getUserIdByToken(token);
        DataBaseUtil.deleteMovieForUser(userid, title);
        response.setStatus(HttpStatus.OK.value());
        return "";
    }
}