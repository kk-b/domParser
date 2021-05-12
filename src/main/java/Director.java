
import java.util.List;

public class Director {

    private final String dirID;

    private final String dirName;

    private final List<Movie> movies;


    public Director(String dirID, String dirName, List movies) {
        this.dirID = dirID;
        this.dirName = dirName;
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
        for (int i = 0; i < movies.size();i++){
            temp += movies.get(i) + " ";
        }
        return temp;
    }

    public String toString() {

        return "Director ID: " + getDirID() + ", " +
                "Director Name: " + getdirName() + ", " +
                "Movies: {" + getDirMovies() + "}";
    }
}
