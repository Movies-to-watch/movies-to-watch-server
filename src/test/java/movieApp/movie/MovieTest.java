package movieApp.movie;

import movieApp.model.movie.Movie;
import movieApp.model.user.User;
import movieApp.utils.MovieUtil;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MovieTest {

    @Test
    public void getMoviesFromTitleInfoTest() {
        final User user = new User("123");
        final Movie movie = new Movie("ABC");
        user.getMovies().put(movie, false);

        assertTrue(movie.equals(MovieUtil.getMovieFromTitle(user, "ABC")));
    }
}
