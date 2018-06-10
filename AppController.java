package hello;

import javafx.util.Pair;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class AppController {
    private final String ORIGINS = "https://movies-to-watch-client.herokuapp.com"; //

    private Map<String, User> users = new HashMap<>();

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movies")
    @ResponseBody
    public List<Pair<MovieJSON, Boolean>> getMovies(String userId) {
        final User user = UserUtil.getUser(users, userId);
        return MovieUtil.getMoviesInfo(user.getMovies());
    }

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movie")
    @ResponseBody
    public MovieJSON getMovieByTitle(String userId, String title) {
        final User user = UserUtil.getUser(users, userId);
        return MovieUtil.getMovieInfo(Objects.requireNonNull(MovieUtil.getMovieFromTitle(user, title)));
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movie/status")
    @ResponseBody
    public Object setMovieStatus(String userId, String title, Boolean status) {
        final User user = UserUtil.getUser(users, userId);
        UserUtil.setMovieStatus(user, title, status);
        return HttpStatus.OK;
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movies/new")
    @ResponseBody
    public Object addMovie(String userId, String title) {
        final User user = UserUtil.getUser(users, userId);
        final String parsedTitle = title.trim().toLowerCase();
        final Movie newMovie = new Movie(parsedTitle);

        if(MovieUtil.getMovieFromTitle(user, parsedTitle) != null) {
            return HttpStatus.ALREADY_REPORTED;
        }

        if(MovieUtil.getMovieInfo(newMovie) == null) {
            return HttpStatus.BAD_REQUEST;
        }

        user.addMovie(newMovie);
        return HttpStatus.CREATED;
    }

    @CrossOrigin(origins = ORIGINS)
    @DeleteMapping("/movies/delete")
    @ResponseBody
    public Object deleteMovie(String userId, String title) {
        final User user = UserUtil.getUser(users, userId);
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
