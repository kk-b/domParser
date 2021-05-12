import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;


public class SAXParserCast extends DefaultHandler {


    List<Movie> directorMovies;
    List<String> actorsinMovie;
    List<Director> myDirector;


    private String tempVal;

    private boolean inDirectorFilms;
    //to maintain context
    private Actor tempActor;
    private Movie tempMovie;
    private Director tempDirector;

    private String dirName;
    private String dirId;
    private String actorName;

    public HashMap<String,String> mysqlActorData = new HashMap<>();
    public HashMap<String,String> mysqlMovieData = new HashMap<>();
    public HashMap<String,String> mysqlMovieData2 = new HashMap<>();



    public SAXParserCast() {
        myDirector = new ArrayList<Director>();

    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Actors '" + myDirector.size() + "'.");

        Iterator<Director> it = myDirector.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("dirfilms")) {
            inDirectorFilms = true;
            tempDirector = new Director();
            directorMovies = new ArrayList<>();


            //create a new instance of actor
        }
        if (qName.equalsIgnoreCase("filmc")) {
            tempMovie = new Movie();
            tempMovie.setDir_id(dirId);
            tempMovie.setDir_name(dirName);

            actorsinMovie = new ArrayList<>();

        }
        if (qName.equalsIgnoreCase("m")) {
            tempActor = new Actor();
            tempActor.setName(actorName);

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("dirfilms")) {
            inDirectorFilms = false;
            tempDirector.setdirectormovies(directorMovies);
            myDirector.add(tempDirector);

        }else if(qName.equalsIgnoreCase("is")){
            dirName = tempVal;
            tempDirector.setDirName(tempVal);
        }
        else if(qName.equalsIgnoreCase("filmc")){
            tempMovie.setActors(actorsinMovie);
            directorMovies.add(tempMovie);

        }

        else if(qName.equalsIgnoreCase("dirid")){
            dirId =tempVal;
            tempDirector.setDirID(tempVal);

        }
        else if (qName.equalsIgnoreCase("f")) {
            tempMovie.setF(tempVal);
        }
        else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        }

        else if (qName.equalsIgnoreCase("a")){
            actorsinMovie.add(tempVal);

        }

    }

    private void hashActorCreate()throws Exception{
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        try{
            String query = "select id,name, birthYear from stars;";


            // Declare our statement
            PreparedStatement actor_statement = connection.prepareStatement(query);

            ResultSet rs_actor = actor_statement.executeQuery();
            String actor_key;

            while (rs_actor.next()){
                actor_key = "";
                String actor_id = rs_actor.getString("id");
                String actor_name = rs_actor.getString("name");
                String actor_year = rs_actor.getString("birthYear");

                actor_key=actor_name;
                actor_key = actor_key.toLowerCase(); //TODO: lower or upper case key?
                mysqlActorData.put(actor_key,actor_year);

            }
            actor_statement.close();
            rs_actor.close();
        }
        catch(Exception e){

        }

    }

    private void insertActorsInMovies() throws Exception{
        hashMovieCreate();
        hashActorCreate();
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        PreparedStatement actorInsertPS = null;
        String actorInsertSQL = null;
        actorInsertSQL = "call insert_starInMoviesxml(?,?,?);";
        int counter = 0;
        try {
            connection.setAutoCommit(false);
            actorInsertPS = connection.prepareStatement(actorInsertSQL);
            for (Director director : myDirector) {
//                System.out.println(mysqlMovieData2);

                for (Movie movie : director.getMoviesMap()) {
                    String movie_key = movie.getTitle()+","+movie.getDirName();
                    movie_key = movie_key.toLowerCase();
                    System.out.println(movie_key);
//                    System.out.println(mysqlMovieData2);
                    if (mysqlMovieData2.containsKey(movie_key)){
                        String[] actors = movie.getActor().split(";");
                        for (String i : actors) {
                            //SQL Insert
                            if (mysqlActorData.containsKey(i.toLowerCase())){
                                actorInsertPS.setString(1, i);
                                actorInsertPS.setString(2, mysqlActorData.get(i.toLowerCase()));
                                actorInsertPS.setString(3, mysqlMovieData2.get(movie_key));
                                actorInsertPS.addBatch();
                            }
//                            String tempActorKey = ;
                        }
                    }else{
                        System.out.println("inconsistency: movie does not exist in database, unable to add: "+movie.getTitle());

                    }

                }
                System.out.println();
            }
            actorInsertPS.executeBatch();
            connection.commit();
        } catch (Exception e){
            System.out.println(e);
        }
        try{
            if (actorInsertPS!=null) actorInsertPS.close();
            if (connection!=null) connection.close();
        } catch (Exception e){
        }
    }


    private void hashMovieCreate()throws Exception{
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        try{
            String query = "select id,title,year,director from movies;";


            // Declare our statement
            PreparedStatement movie_statement = connection.prepareStatement(query);

            ResultSet rs_movie = movie_statement.executeQuery();
            String movie_key;

            while (rs_movie.next()){
                movie_key = "";
                String movie_id = rs_movie.getString("id");
                String movie_title = rs_movie.getString("title");
                String movie_year = rs_movie.getString("year");
                String movie_director = rs_movie.getString("director");

                movie_key=movie_title+","+movie_year+","+movie_director;
                movie_key = movie_key.toLowerCase(); //TODO: lower or upper case key?
                mysqlMovieData.put(movie_key,movie_id);
                movie_key=movie_title+","+movie_director;
                movie_key = movie_key.toLowerCase();
                mysqlMovieData2.put(movie_key, movie_id);

            }
            movie_statement.close();
            rs_movie.close();

        }
        catch(Exception e){
            System.out.println(e);

        }


    }



    public static void main(String[] args) throws Exception {
        SAXParserCast spa = new SAXParserCast();
        spa.runExample();
        spa.insertActorsInMovies();
    }

}