package movieApp.model.user;

import movieApp.model.movie.Movie;

import java.util.HashMap;
import java.util.Set;

public class User {
    private String userId;
    private HashMap<Movie, Boolean> movies;

    public User(String userId) {
        this.userId = userId;
        this.movies = new HashMap<>();
    }

    public void addMovie(String title) {
        movies.put(new Movie(title), false);
    }

    public HashMap<Movie, Boolean> getMovies() {
        return movies;
    }

    public void setMovieStatus (String title, boolean newValue) {
        Set<Movie> userMovies = movies.keySet();
        for(Movie movie : userMovies) {
            if (movie.getTitle().equals(title)) {
                movies.replace(movie, newValue);
            }
        }
    }
}
