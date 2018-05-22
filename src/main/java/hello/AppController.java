package hello;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AppController {
    private final String prefixHttp = "http://www.omdbapi.com/?t=";
    private final String suffixHttp = "&apikey=ecf845f8";
    private final String ORIGINS = "https://movies-to-watch-client.herokuapp.com"; //

    private Map<String, User> users = new HashMap<>();

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movies")
    @ResponseBody
    public List<MovieJSON> getMovies(String userId) {
        final User user = getUser(userId);
        return getMoviesInfo(user.getMovies());
    }

    @CrossOrigin(origins = ORIGINS)
    @GetMapping("/movie")
    @ResponseBody
    public MovieJSON getMovieByTitle(String userId, String title) {
        final User user = getUser(userId);
        return getMovieInfo(getMovieFromTitle(user, title).get());
    }

    @CrossOrigin(origins = ORIGINS)
    @PostMapping("/movies/new")
    @ResponseBody
    public Object addMovie(String userId, String title) {
        final User user = getUser(userId);
        final String parsedTitle = title.trim().toLowerCase();
        final Movie newMovie = new Movie(parsedTitle);

        if(getMovieFromTitle(user, parsedTitle).isPresent())
            return HttpStatus.ALREADY_REPORTED;

        if(getMovieInfo(newMovie) == null) {
            return HttpStatus.BAD_REQUEST;
        }

        user.addMovie(newMovie);
        return HttpStatus.CREATED;
    }

    @CrossOrigin(origins = ORIGINS)
    @DeleteMapping("/movies/delete")
    @ResponseBody
    public Object deleteMovie(String userId, String title) {
        final User user = getUser(userId);
        user.deleteMovie(getMovieFromTitle(user, title).get());
        return HttpStatus.OK;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private User getUser(String userId){
        if(!users.containsKey(userId))
            users.put(userId, new User(userId));

        return users.get(userId);
    }

    private Optional<Movie> getMovieFromTitle(User user, String title) {
        String parsedTitle = title.trim().toLowerCase();
        return user.getMovies().stream().filter(movie -> movie.getTitle().equals(parsedTitle)).findFirst();
    }

    private List<MovieJSON> getMoviesInfo(List<Movie> movies) {
        return movies.stream().map(this::getMovieInfo).collect(Collectors.toList());
    }

    private HttpHeaders createHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        return headers;
    }

    private MovieJSON getMovieInfo(Movie movie){
        ResponseEntity<String> res = new RestTemplate().exchange(
            String.format("%s%s%s", prefixHttp, movie.getTitle(), suffixHttp),
            HttpMethod.GET,
            new HttpEntity<>("parameters", createHeaders()),
            String.class
        );

        return res.getStatusCode() == HttpStatus.OK
            ? createMovieJSON(res.getBody())
            : null;
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
        final JSONArray ratings = obj.getJSONArray("Ratings");
        final String awards = obj.getString("Awards");
        final String plot = obj.getString("Plot");

        return new MovieJSON(id, title, poster, year, runtime, genre, director, production,
            website, actors, ratings, awards, plot);
    }
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
