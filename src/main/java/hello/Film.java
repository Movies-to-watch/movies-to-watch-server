package hello;

public class Film {

    private final String name;
    private final String content;

    public Film(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
