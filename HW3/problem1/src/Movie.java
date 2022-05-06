
import java.util.HashMap;
import java.util.Map;

public class Movie {
    private String title;
    private String[] tags;

    public Movie(String title) { this.title = title;}
    public Movie(String title, String[] tags){
        this.title = title;
        this.tags = tags;
    }
    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public String[] getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o){
        if(! (o instanceof Movie)) return false;
        return title.equals(((Movie) o).title);
    }
    @Override
    public int hashCode(){
        return title.hashCode();
    }
}
