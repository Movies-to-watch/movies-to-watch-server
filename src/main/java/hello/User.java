package hello;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private List<Movie> movies;

    public User(String userId) {
        this.userId = userId;
        this.movies = new ArrayList<>();
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public void deleteMovie(Movie movie) {
        movies.remove(movie);
    }

    public String getUserId() {
        return userId;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
