package hello;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class StatusMovieJSON{

    private MovieJSON movieJSON;
    private boolean status;

    public StatusMovieJSON(MovieJSON movieJSON, boolean status) {
        this.movieJSON = movieJSON;
        this.status = status;
    }
}
