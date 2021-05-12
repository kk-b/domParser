
import java.util.List;

public class Director {

    private String dirID;

    private  String dirName;

    private List<Movie> movies;


    public Director() {
        this.dirID = "";
        this.dirName = "";
        this.movies = null;

    }

    public void setDirID(String dirID){
        this.dirID = dirID;
    }

    public void setDirName(String dirName){
        this.dirName = dirName;
    }
    public void setdirectormovies(List<Movie> movies){
        this.movies = movies;
    }

    public List<Movie> getMoviesMap(){
        return movies;
    }

    public String getDirID() {
        return dirID;
    }

    public String getdirName() {
        return dirName;
    }


    public String getDirMovies(){
        String temp = "";

        if (movies !=null) {
            temp = "";
            for (int i = 0; i < movies.size(); i++) {
                temp += movies.get(i) + " ";
            }
        }
        return temp;
    }

    public String toString() {

        return "Director ID: " + getDirID() + ", " +
                "Director Name: " + getdirName() + ", " +
                "Movies: {" + getDirMovies() + "}";
    }
}
