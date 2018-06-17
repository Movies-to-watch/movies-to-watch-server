package movieApp;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import movieApp.model.movie.Movie;
import movieApp.model.movie.MovieJSON;
import movieApp.model.movie.StatusMovieJSON;
import movieApp.model.user.User;
import movieApp.utils.MovieUtil;
import movieApp.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Controller
public class AppController {
    private final String ORIGINS = "https://movies-to-watch-client.herokuapp.com";
    private Map<String, User> users = new HashMap<>();

    private final Environment env;

    @Autowired
    public AppController(Environment env) {
        this.env = env;
    }

    private String getClientId(){
        String CLIENT_ID = env.getProperty("CLIENT_ID");
        if(CLIENT_ID == null){
            try {
                BufferedReader br = new BufferedReader(new FileReader("config"));
                CLIENT_ID = br.readLine();
            } catch (Exception e) {e.printStackTrace();}
        }
        return CLIENT_ID;
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/login")
    @ResponseBody
    public String loginUser(String token, HttpServletResponse response) {
        if(token.equals("test")){
            users.put(token, new User("test"));
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

            for (Map.Entry<String, User> userEntry: users.entrySet()) {
                User user = userEntry.getValue();

                if(user.getUserId().equals(userId)){
                    users.remove(userEntry.getKey());
                    users.put(token, user);
                }
            }
            users.computeIfAbsent(token, k -> new User(userId));

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
        //logout
        response.setStatus(HttpStatus.OK.value());
        return "";
    }

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movies")
    @ResponseBody
    public List<StatusMovieJSON> getMovies(String token, HttpServletResponse response) {
        final User user = UserUtil.getUser(users, token);

        response.setStatus(HttpStatus.OK.value());
        return MovieUtil.getMoviesInfo(user.getMovies());
    }

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movie")
    @ResponseBody
    public MovieJSON getMovieByTitle(String token, String title, HttpServletResponse response) {
        final User user = UserUtil.getUser(users, token);

        response.setStatus(HttpStatus.OK.value());
        return MovieUtil.getMovieInfo(Objects.requireNonNull(MovieUtil.getMovieFromTitle(user, title)));
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movie/status")
    @ResponseBody
    public Object setMovieStatus(String token, String title, Boolean status, HttpServletResponse response) {
        final User user = UserUtil.getUser(users, token);
        UserUtil.setMovieStatus(user, title, status);

        response.setStatus(HttpStatus.OK.value());
        return "";
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movies/new")
    @ResponseBody
    public Object addMovie(String token, String title, HttpServletResponse response) {
        final User user = UserUtil.getUser(users, token);
        final String parsedTitle = title.trim().toLowerCase();
        final Movie newMovie = new Movie(parsedTitle);

        MovieJSON movieJSON = MovieUtil.getMovieInfo(newMovie);
        if(movieJSON == null || movieJSON.getId().equals("")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "Movie with such title does not exist.";
        }

        if(MovieUtil.getMovieFromTitle(user, movieJSON.getTitle()) != null) {
            response.setStatus(HttpStatus.ALREADY_REPORTED.value());
            return "Movie with such title is already added.";
        }

        user.addMovie(movieJSON.getTitle());
        response.setStatus(HttpStatus.CREATED.value());
        return "";
    }

    @CrossOrigin(origins = ORIGINS)
    @DeleteMapping("/movies/delete")
    @ResponseBody
    public Object deleteMovie(String token, String title, HttpServletResponse response) {
        final User user = UserUtil.getUser(users, token);
        user.deleteMovie(MovieUtil.getMovieFromTitle(user, title));

        response.setStatus(HttpStatus.OK.value());
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
