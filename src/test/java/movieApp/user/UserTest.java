package movieApp.user;

import movieApp.model.movie.Movie;
import movieApp.model.user.User;
import static org.junit.Assert.*;

import org.junit.Test;

public class UserTest {

    @Test
    public void addMovieMapSizeTest() {
        final User user1 = new User("123");
        final User user2 = new User("123");
        final Movie movie = new Movie("ABC");

        user1.getMovies().put(movie, false);
        user2.addMovie("ABC");

        assertTrue(user1.getMovies().size() == user2.getMovies().size());
    }

    @Test
    public void addMovieMapContentTest() {
        final User user1 = new User("123");
        final User user2 = new User("123");
        final Movie movie = new Movie("ABC");

        user1.getMovies().put(movie, false);
        user2.addMovie("ABC");

        assertTrue(user1.getMovies().keySet().stream().findFirst().get().getTitle()
                .equals(user2.getMovies().keySet().stream().findFirst().get().getTitle()));
    }

    @Test
    public void setMovieStatusTest() {
        final User user1 = new User("123");
        final User user2 = new User("123");

        user1.addMovie("ABC");
        user2.addMovie("ABC");

        final Movie movie = user1.getMovies().keySet().stream().findFirst().get();
        user1.getMovies().replace(movie, true);
        user2.setMovieStatus("ABC", true);

        assertTrue(user1.getMovies().entrySet().stream().findFirst().get().getValue()
                .equals(user2.getMovies().entrySet().stream().findFirst().get().getValue()));
    }
}