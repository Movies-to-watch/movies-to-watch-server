package movieApp.model.movie;

import movieApp.model.user.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public final class MovieUtil {

    private static final String prefixHttp = "http://www.omdbapi.com/?t=";
    private static final String suffixHttp = "&apikey=ecf845f8";

    public static List<StatusMovieJSON> getMoviesInfo(HashMap<Movie, Boolean> movies) {
        List<StatusMovieJSON> userMovies = new ArrayList<>();
        for (Movie movie : movies.keySet()) {
            userMovies.add(new StatusMovieJSON(getMovieInfo(movie), movies.get(movie)));
        }
        return userMovies;
    }

    public static MovieJSON getMovieInfo(Movie movie){
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

    private static MovieJSON createMovieJSON(String json) {
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

    public static Movie getMovieFromTitle(User user, String title) {
        Set<Movie> userMovies = user.getMovies().keySet();
        for(Movie movie : userMovies) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null;
    }

    private static HttpHeaders createHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        return headers;
    }
}
