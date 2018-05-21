package hello;

import java.util.ArrayList;
import java.util.List;

public class User {

    private int userId;
    private List<Movie> movies = new ArrayList<>();

    public User(int userId) {
        this.userId = userId;
        // TODO" te 3 poniżej dodane dla mocka
        addMovie(new Movie("gladiator"));
        addMovie(new Movie("batman"));
        addMovie(new Movie("juno"));
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public void deleteMovie(Movie movie) {
        movies.remove(movie);
    }

    public int getUserId() {
        return userId;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
