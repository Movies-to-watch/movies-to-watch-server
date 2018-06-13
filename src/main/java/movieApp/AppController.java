package movieApp;

import movieApp.model.movie.Movie;
import movieApp.model.movie.MovieJSON;
import movieApp.utils.MovieUtil;
import movieApp.model.movie.StatusMovieJSON;
import movieApp.model.user.User;
import movieApp.utils.UserUtil;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class AppController {
    private final String ORIGINS = "https://movies-to-watch-client.herokuapp.com";

    private Map<String, User> users = new HashMap<>();

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/login")
    @ResponseBody
    public String loginUser(String userId) {
        final String uniqueToken = UserUtil.getUniqueToken();
        users.put(uniqueToken, new User(userId));
        return uniqueToken;
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/logout")
    @ResponseBody
    public Object logoutUser(String token) {
        users.remove(token);
        return HttpStatus.OK;
    }

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movies")
    @ResponseBody
    public List<StatusMovieJSON> getMovies(String token) {
        final User user = UserUtil.getUser(users, token);
        return MovieUtil.getMoviesInfo(user.getMovies());
    }

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movie")
    @ResponseBody
    public MovieJSON getMovieByTitle(String token, String title) {
        final User user = UserUtil.getUser(users, token);
        return MovieUtil.getMovieInfo(Objects.requireNonNull(MovieUtil.getMovieFromTitle(user, title)));
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movie/status")
    @ResponseBody
    public Object setMovieStatus(String token, String title, Boolean status) {
        final User user = UserUtil.getUser(users, token);
        UserUtil.setMovieStatus(user, title, status);
        return HttpStatus.OK;
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movies/new")
    @ResponseBody
    public Object addMovie(String token, String title) {
        final User user = UserUtil.getUser(users, token);
        final String parsedTitle = title.trim().toLowerCase();
        final Movie newMovie = new Movie(parsedTitle);

        if(MovieUtil.getMovieFromTitle(user, parsedTitle) != null) {
            return HttpStatus.ALREADY_REPORTED;
        }

        MovieJSON movieJSON = MovieUtil.getMovieInfo(newMovie);
        if(movieJSON == null || movieJSON.getId().equals("")) {
            return HttpStatus.BAD_REQUEST;
        }

        user.addMovie(movieJSON.getTitle());
        return HttpStatus.CREATED;
    }

    @CrossOrigin(origins = ORIGINS)
    @DeleteMapping("/movies/delete")
    @ResponseBody
    public Object deleteMovie(String token, String title) {
        final User user = UserUtil.getUser(users, token);
        user.deleteMovie(MovieUtil.getMovieFromTitle(user, title));
        return HttpStatus.OK;
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
