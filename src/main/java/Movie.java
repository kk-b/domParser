import java.util.ArrayList;
import java.util.List;


public class Movie {

    private final String dir;

    private final String is;

    private final String f;

    private List<String> actors = null;

    private final String title;

    private List<String> genres;

    private int year = -1;




    public Movie(String dir, String is, String f, String title) {
        this.dir = dir;
        this.is = is;
        this.f = f;
        this.title = title;
    }

    public void setActors(List<String> actors){
        this.actors= actors;
    }
    public void setGenres(List<String> genres){
        this.genres = genres;
    }

    public void setYear(int year){
        this.year = year;
    }

    public String getDirID() {
        return dir;
    }

    public String getDirName() {
        return is;
    }

    public String getF() {
        return f;
    }

    public String getActor() {
        String temp = "(";
        for (int i = 0; i < actors.size();i++){
            temp += actors.get(i) + ";";
        }
        temp += ")";
        return temp;
    }

    public String getTitle(){
        return title;
    }

    public String getGenres() {
        String temp = "";
        for (int i = 0; i < genres.size();i++){
            temp += genres.get(i) + ";";
        }
        return temp;
    }

    public int getYear(){
        return year;
    }

    public String toString() {

        String temp = "(Director ID:" + getDirID() + ", " +
                "Director Name:" + getDirName() + ", " +
                "Film ID:" + getF() + ", " +
                "Film Title: " + getTitle();
        if (genres != null){
            temp += ", Genres: " + getGenres();
        }
        if (year != -1){
            temp += ", Year: " + getYear();
        }
        if (actors != null){
            temp += ", Actors: " + getActor();
        }
        temp += ")";
        return temp;
    }
}
