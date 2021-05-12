
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
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


public class SAXParserMovie extends DefaultHandler {

    List<Movie> myMovie;

    private String tempVal;

    private boolean inDirectorFilms;
    public HashMap<String,String> mysqlMovieData = new HashMap<>();

    //to maintain context
    private Movie tempMovie;

    private String dirName;
    private String dirId;
    private List<String> genres;
    public HashMap<String,String> mysqlMovieData2 = new HashMap<>();


    public SAXParserMovie() {
        myMovie = new ArrayList<Movie>();
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
            sp.parse("mains243.xml", this);

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

//        System.out.println("No of Employees '" + myMovie.size() + "'.");
//
//        Iterator<Movie> it = myMovie.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("directorfilms")) {
            inDirectorFilms = true;
            //create a new instance of employee
        }
        if (qName.equalsIgnoreCase("film")) {
            tempMovie = new Movie();
            tempMovie.setDir_id(dirId);
            tempMovie.setDir_name(dirName);

        }
        if (qName.equalsIgnoreCase("cats")) {
            genres = new ArrayList<>();

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("directorfilms")) {
            inDirectorFilms = false;
        }else if(qName.equalsIgnoreCase("dirname")){
            dirName = tempVal;
        }
        else if(qName.equalsIgnoreCase("dirid")){
            dirId =tempVal;
        }
        else if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setF(tempVal);
        }
        else if (qName.equalsIgnoreCase("year")) {
            int year = -1;

            if (tempVal !="") {
                try {
                    year = Integer.parseInt(tempVal);

                } catch (Exception e){

                }
                tempMovie.setYear(year);
            }
        }
        else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        }
        else if (qName.equalsIgnoreCase("cat")) {
            genres.add(tempVal);

        }
        else if (qName.equalsIgnoreCase("cats")) {
            tempMovie.setGenres(genres);
        }
        else if (qName.equalsIgnoreCase("film")) {
            myMovie.add(tempMovie);

        }
    }

    private void insertMovie() throws Exception {
        hashMovieCreate();

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);



        PreparedStatement movieInsertPS = null;
        String movieInsertSQL = null;

        int[] iNoRows = null;

        movieInsertSQL = "call add_moviexml(?,?,?);";
        try{
            movieInsertPS = connection.prepareStatement(movieInsertSQL);
            for (Movie movie: myMovie){
                String movie_key = movie.getTitle()+","+movie.getYear()+","+movie.getDirName();
                movie_key = movie_key.toLowerCase();
                if (mysqlMovieData.containsKey(movie_key)){
                    System.out.println("inconsistency: movie already exists title: "+movie.getTitle());
                }
                else if (movie.getTitle() == null){
                    System.out.println("inconsistency: movie title (<t>) cannot be empty: "+movie.getTitle());
                }
                else if (movie.getDirName() == null){
                    System.out.println("inconsistency: director field (<dirname>) cannot be empty: "+movie.getDirName());
                }
                else if (movie.getYear() == -1){
                    System.out.println(movie.toString());
                    System.out.println("inconsistency: movie year field (<year>) cannot be empty: "+movie.getYear());
                }
                else{

                    movieInsertPS.setString(1,movie.getTitle());
                    movieInsertPS.setString(2,String.valueOf(movie.getYear()));
                    movieInsertPS.setString(3,movie.getDirName());
//                    movieInsertPS.addBatch();

                }

            }
            movieInsertPS.executeUpdate();
            movieInsertPS.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }

//        try {
//            if(movieInsertPS!=null) movieInsertPS.close();
//            if(connection!=null) connection.close();
//        } catch(Exception e) {
//            connection.close();
//
////            e.printStackTrace();
//        }
//        try{
//            if (movieInsertPS!=null) movieInsertPS.close();
//            if (connection!=null) connection.close();
//        } catch (Exception e){
//            System.out.println(e);
//        }
//        hashMovieCreate();
        connection.close();

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
            connection.close();

        }
        catch(Exception e){
            connection.close();

//            System.out.println(e);

        }
        connection.close();


    }



    public static void main(String[] args) throws Exception {
        PrintStream o = new PrintStream(new File("B.txt"));
        PrintStream console = System.out;
        System.setOut(o);
        SAXParserMovie spa = new SAXParserMovie();
        spa.runExample();
        spa.insertMovie();
    }

}
