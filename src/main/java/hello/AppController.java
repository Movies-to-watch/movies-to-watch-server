package hello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;



@Controller
public class AppController {

    private final String prefixHttp = "http://www.omdbapi.com/?t=";
    private final String suffixHttp = "&apikey=ecf845f8";
    private List<User> users = new ArrayList<>();


    @GetMapping("/movies")
    @ResponseBody
    public List<MovieJSON> getMovies(@RequestParam(name="userId", required=false) String userId) {
        users.add(new User(Integer.parseInt(userId))); // TODO: to jest dodane dla mocka, zeby był jakis uzytkownik
        final User user = getUserFromRequest(userId);
        final List<Movie> movies = getUserMovies(user);
        return getMoviesInfo(movies);
    }

    @GetMapping("/movie")
    @ResponseBody
    public MovieJSON getMovieByTitle(@RequestParam(name="userId", required=false) String userId, String title) {
        final User user = getUserFromRequest(userId);
        final Movie movie = getMovieFromTitle(user, title);
        final List<Movie> movies = new ArrayList<Movie>();
        movies.add(movie);
        return getMoviesInfo(movies).get(0);
    }

    @PostMapping("/movies/new")
    @ResponseStatus(HttpStatus.OK)
    public String addMovie(@RequestParam(name="userId", required=false) String userId, String title) {
        final User user = getUserFromRequest(userId);
        user.addMovie(new Movie(title));
        return "OK";
    }

    @DeleteMapping("/movie")
    @ResponseStatus(HttpStatus.OK)
    public String deleteMovie(@RequestParam(name="userId", required=false) String userId, String title) {
        final User user = getUserFromRequest(userId);
        user.deleteMovie(getMovieFromTitle(user, title));
        return "OK";
    }

    private Movie getMovieFromTitle(User user, String title) {
        for(Movie movie : user.getMovies()) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null;
    }

    private User getUserFromRequest(String userId) {
        for (User user: users) {
            if (user.getUserId() == Integer.parseInt(userId)) {
                return user;
            }
        }
        return null;
    }

    private List<Movie> getUserMovies(User user) {
        return user.getMovies();
    }

    private List<MovieJSON> getMoviesInfo(List<Movie> movies) {
        List<MovieJSON> moviesInfoList = new ArrayList<>();
        for (Movie movie: movies) {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            String url = prefixHttp + movie.getTitle() + suffixHttp;
            ResponseEntity<String> res = rt.exchange(url, HttpMethod.GET, entity, String.class);
            moviesInfoList.add(createMovieJSON(res.getBody()));
        }
        return moviesInfoList;
    }

    private MovieJSON createMovieJSON(String json) {
        JSONObject obj = new JSONObject(json);
        final String id = obj.getString("imdbID");
        final String title = obj.getString("Title");
        final String poster = obj.getString("Poster");
        final String year = obj.getString("Year");
        final String runtime = obj.getString("Runtime");
        final String genre = obj.getString("Genre");
        final String director = obj.getString("Director");
        final String production = obj.getString("Production");
        final String website = obj.getString("Website");
        final String actors = obj.getString("Actors");
        final String ratings = obj.getString("imdbRating");
        final String awards = obj.getString("Awards");
        final String plot = obj.getString("Plot");

        // TODO: nie usuwaj printów, bo może mi się do debugu przydać jeszcze
        System.out.println(id);
        System.out.println(title);
        System.out.println(poster);
        System.out.println(year);
        System.out.println(runtime);
        System.out.println(genre);
        System.out.println(director);
        System.out.println(production);
        System.out.println(website);
        System.out.println(actors);
        System.out.println(ratings);
        System.out.println(awards);
        System.out.println(plot);

        return new MovieJSON(id, title, poster, year, runtime, genre, director, production,
                website, actors, ratings, awards, plot);
    }

}
