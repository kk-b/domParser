import java.util.ArrayList;
import java.util.List;


public class Movie {

    private String dir_name;

    private String dir_id;

    private String f;

    private List<String> actors = null;

    private String title;

    private List<String> genres;

    private int year = -1;




    public Movie() {
        this.dir_name = "";
        this.dir_id = "";
        this.f = "";
        this.title = "";
    }

    public void setDir_name(String dir){
        this.dir_name = dir;
    }
    public void setDir_id(String is){
        this.dir_id = is;
    }
    public void setF(String f){
        this.f = f;
    }
    public void setTitle(String title){
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
        return dir_id;
    }

    public String getDirName() {
        return dir_name;
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
