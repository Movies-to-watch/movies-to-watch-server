package movieApp.utils;

import movieApp.model.movie.Movie;
import movieApp.model.movie.MovieJSON;
import movieApp.model.movie.StatusMovieJSON;
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
        final String id = JsonUtil.getParsedField(obj, "imdbID");
        final String title = JsonUtil.getParsedField(obj,"Title");
        final String poster = JsonUtil.getParsedField(obj,"Poster");
        final String year = JsonUtil.getParsedField(obj,"Year");
        final String runtime = JsonUtil.getParsedField(obj,"Runtime");
        final String genre = JsonUtil.getParsedField(obj,"Genre");
        final String director = JsonUtil.getParsedField(obj,"Director");
        final String production = JsonUtil.getParsedField(obj,"Production");
        final String website = JsonUtil.getParsedField(obj,"Website");
        final String actors = JsonUtil.getParsedField(obj,"Actors");
        final JSONArray ratings = JsonUtil.getParsedJsonArray(obj,"Ratings");
        final String awards = JsonUtil.getParsedField(obj,"Awards");
        final String plot = JsonUtil.getParsedField(obj,"Plot");

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
