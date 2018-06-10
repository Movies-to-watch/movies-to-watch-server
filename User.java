package hello;

import java.util.HashMap;
import java.util.Set;

public class User {
    private String userId;
    private HashMap<Movie, Boolean> movies;

    public User(String userId) {
        this.userId = userId;
        this.movies = new HashMap<>();
    }

    public void addMovie(Movie movie) {
        movies.put(movie, false);
    }

    public void deleteMovie(Movie movie) {
        movies.remove(movie);
    }

    public String getUserId() {
        return userId;
    }

    public HashMap<Movie, Boolean> getMovies() {
        return movies;
    }

    public void setMovieStatus (String title, boolean newValue) {
        Set<Movie> userMovies = movies.keySet();
        for(Movie movie : userMovies) {
            if (movie.getTitle().equals(title.trim().toLowerCase())) {
                movies.replace(movie, newValue);
            }
        }
    }
}
