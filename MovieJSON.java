package hello;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.json.JSONArray;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MovieJSON implements Serializable{
    private final String id;
    private final String title;
    private final String poster;
    private final String year;
    private final String runtime;
    private final String genre;
    private final String director;
    private final String production;
    private final String website;
    private final String actors;
    private final String ratings;
    private final String awards;
    private final String plot;

    public MovieJSON(String id, String title, String poster, String year, String runtime, String genre,
                 String director, String production, String website, String actors, JSONArray ratings,
                 String awards, String plot) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.year = year;
        this.runtime = runtime;
        this.genre = genre;
        this.director = director;
        this.production = production;
        this.website = website;
        this.actors = actors;
        this.ratings = ratings.toString();
        this.awards = awards;
        this.plot = plot;
    }
}
