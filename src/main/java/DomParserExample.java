import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.PreparedStatement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.PrintStream;
import java.sql.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DomParserExample {

    public List<Actor> actors = new ArrayList<>();
    public List<Director> directors = new ArrayList<>();
    public List<Movie> movies = new ArrayList<>();
    public HashMap<String,String> movieAbbreviations = new HashMap<>();
    public HashMap<String,String> mysqlMovieData = new HashMap<>();
    public HashMap<String,String> mysqlMovieData2 = new HashMap<>();
    public HashMap<String,String> mysqlGenreData = new HashMap<>();
    public HashMap<String,String> mysqlActorData = new HashMap<>();




    Document dom;

    public void run(String file, String tag) {

        // parse the xml file and get the dom object
        parseXmlFile(file);

        // get each employee element and create a Employee object
        parseDocument(tag);

        // iterate through the list and print the data
        printData();

    }

    private void parseXmlFile(String file) {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse(file);

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument(String tag) {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName(tag);
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                // get the employee element
                Element element = (Element) nodeList.item(i);

                // get the Employee object

                if (tag.equals("actor")){
                    Actor actor = parseActor(element);
                    actors.add(actor);
                }
                else if (tag.equals("dirfilms")){
                    Director director = parseDirectors(element);
                    directors.add(director);
                }
                else if (tag.equals("directorfilms")){
                    parseMovieInfo(element);
                }

            }
        }
    }

    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private Actor parseActor(Element element) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        int dob = -1;
        String name = getTextValue(element, "stagename");

        try {
            dob = getIntValue(element, "dob");

        } catch (Exception e){
//            System.out.println("1");
        }
        // create a new Employee with the value read from the xml nodes
        return new Actor(name, dob);
    }

    private Director parseDirectors(Element element){
        List<Movie> tempMovies = new ArrayList<>();

        String dirID = getTextValue(element, "dirid");
        String dirName = getTextValue(element, "is");
        NodeList nodeList = element.getElementsByTagName("filmc");
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                // get the employee element
                Element e = (Element) nodeList.item(i);
                Movie movie = parseMovie(e, dirID, dirName);
                tempMovies.add(movie);
            }
        }
        return new Director(dirID, dirName, tempMovies);
    }

    private Movie parseMovie(Element element, String dirID, String dirName){
        String movieID = "";
        String title = "";
        List<String> actors = new ArrayList<>();

        NodeList nodeList = element.getElementsByTagName("m");
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                // get the employee element
                try{
                    Element e = (Element) nodeList.item(i);
                    movieID = getTextValue(e, "f");
                    title = getTextValue(e, "t");
                    String actor = getTextValue(e, "a");
                    actors.add(actor);
                } catch (Exception e){
                }


            }
        }
        Movie tempMovie = new Movie(dirID, dirName, movieID, title);
        tempMovie.setActors(actors);
        return tempMovie;
    }

    private void parseMovieInfo(Element element){
        String DirID = getTextValue(element, "dirid");
        String DirName = getTextValue(element, "dirname");
        NodeList nodeList = (element.getElementsByTagName("films"));

        ////////////
        Element e1 = (Element) nodeList.item(0);
        NodeList nodeList1 = (e1.getElementsByTagName("film"));

        //////////////

        if (nodeList1 != null) {
            for (int i = 0; i < nodeList1.getLength(); i++) {

                // get the employee element
                Element e = (Element) nodeList1.item(i);
                parseFilms(e,DirID,DirName);
            }
        }
    }

    private void parseFilms(Element element, String dirID, String dirName) {
        String title = "";
        try {
            String filmID = getTextValue(element, "fid");
            title = getTextValue(element, "t");
            int year = getIntValue(element, "year");

            String x = dirID + " " + dirName + " " + filmID + " " + title + " " + year;
//            System.out.println(x);
            NodeList nodeList = element.getElementsByTagName("cat");

            List<String> genres = null;
            if (nodeList != null) {

                genres = new ArrayList<>();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    genres.add(nodeList.item(i).getFirstChild().getNodeValue());

                }
            }

            Movie temp = new Movie(dirID, dirName, filmID, title);
            temp.setYear(year);
            temp.setGenres(genres);
            movies.add(temp);


        } catch (Exception e) {
            System.out.println("Crashed: title given (<t>) is not acceptable");
        }
    }


    /**
     * It takes an XML element and the tag name, look for the tag and get
     * the text content
     * i.e for <Employee><Name>John</Name></Employee> xml snippet if
     * the Element points to employee node and tagName is name it will return John
     */
    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            // here we expect only one <Name> would present in the <Employee>
            textVal = nodeList.item(0).getFirstChild().getNodeValue();

        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

//        System.out.println("Total parsed " + actors.size() + " employees");
//
//        for (Actor actor : actors) {
//            System.out.println("\t" + actor.toString());
//        }
        for (Director director : directors){
            System.out.println("\t" + director.toString());
        }
//        for (Movie movie : movies){
//            System.out.println("\t" + movie.toString());
//        }
    }

    private void insertStars() throws Exception{
        hashActorCreate();
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        try{


            for (Actor actor : actors){
                String query = "select getStarID() as star_id";


                // Declare our statement
                PreparedStatement star_statement = connection.prepareStatement(query);

                ResultSet rs_star = star_statement.executeQuery();
                String star_id= "";

                while (rs_star.next()){
                    star_id = rs_star.getString("star_id");
                }
//                System.out.println(star_id);
                String starInsert = "";
                if (actor.getDob() == -1){
                    starInsert = "INSERT into stars(id, name) values (?, ?);";
                    PreparedStatement statementStar = connection.prepareStatement(starInsert);
                    statementStar.setString(1,star_id);
                    statementStar.setString(2,actor.getName());
                    statementStar.executeUpdate();
                    statementStar.close();
                }else{
                    starInsert = "INSERT into stars(id, name, birthYear) values (?, ?, ?);";
                    if (mysqlActorData.containsKey(actor.getName().toLowerCase())){
                        if (mysqlActorData.get(actor.getName().toLowerCase()) != null){
                            if (mysqlActorData.get(actor.getName().toLowerCase()).equals(String.valueOf(actor.getDob()))){
                                System.out.println("inconsistency: actor exists in database already! " + actor.getName());
                            }
                            else{
                                PreparedStatement statementStar = connection.prepareStatement(starInsert);
                                statementStar.setString(1,star_id);
                                statementStar.setString(2,actor.getName());
                                statementStar.setString(3,String.valueOf(actor.getDob()));
                                statementStar.executeUpdate();
                                statementStar.close();
                            }
                        }else{
                            PreparedStatement statementStar = connection.prepareStatement(starInsert);
                            statementStar.setString(1,star_id);
                            statementStar.setString(2,actor.getName());
                            statementStar.setString(3,String.valueOf(actor.getDob()));
                            statementStar.executeUpdate();
                            statementStar.close();
                        }

                    }else{
                        PreparedStatement statementStar = connection.prepareStatement(starInsert);
                        statementStar.setString(1,star_id);
                        statementStar.setString(2,actor.getName());
                        statementStar.setString(3,String.valueOf(actor.getDob()));
                        statementStar.executeUpdate();
                        statementStar.close();
                    }
                }

                star_statement.close();
                rs_star.close();
//                System.out.println("\t" + actor.toString());
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        connection.close();
    }

    private void insertGenre() throws Exception {
        hashGenreCreate();

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        PreparedStatement genreInsertPS = null;
        String genreInsertSQL = null;
        genreInsertSQL ="call add_genrexml(?);";
        try{
            connection.setAutoCommit(false);
            genreInsertPS = connection.prepareStatement(genreInsertSQL);
            for (Movie movie: movies){
                String[] genres = movie.getGenres().split(";");
                for(int i =0;i<genres.length;i++){
                    String currentGenre = genres[i].toLowerCase();
                    if (currentGenre == ""){
                        System.out.println("inconsistency: genre given in (<cat>) is null: " + currentGenre);
                    }
                    else if (!movieAbbreviations.containsKey(currentGenre)){
                        System.out.println("inconsistency: genre given (<cat>) is not part of DND movie category: " + currentGenre);
                    }
                    else if (mysqlGenreData.containsKey(movieAbbreviations.get(currentGenre))){
                        System.out.println("inconsistency: genre already exists: " + movieAbbreviations.get(currentGenre));
                    }
                    else{
                        genreInsertPS.setString(1,movieAbbreviations.get(currentGenre));
                        genreInsertPS.addBatch();
                        mysqlGenreData.put(movieAbbreviations.get(currentGenre), "0");
                    }
                }

            }
            genreInsertPS.executeBatch();
            connection.commit();
        } catch (Exception e){
            System.out.println(e);
        }
        try{
            if (genreInsertPS!=null) genreInsertPS.close();
            if (connection!=null) connection.close();
        } catch (Exception e){
            System.out.println(e);
        }
        connection.close();
        hashGenreCreate();

    }

    private void hashGenreCreate() throws Exception {
        // Add abbreviations to hashmap for later usage
        movieAbbreviations.put("susp", "Thriller");
        movieAbbreviations.put("cnr", "Cops and Robbers");
        movieAbbreviations.put("dram", "Drama");
        movieAbbreviations.put("west", "Western");
        movieAbbreviations.put("myst", "Mystery");
        movieAbbreviations.put("s.f.", "Sci-Fi");
        movieAbbreviations.put("advt", "Adventure");
        movieAbbreviations.put("horr", "Horror");
        movieAbbreviations.put("romt", "Romantic");
        movieAbbreviations.put("comd", "Comedy");
        movieAbbreviations.put("musc", "Musical");
        movieAbbreviations.put("docu", "Documentary");
        movieAbbreviations.put("porn", "Pornography, including soft");
        movieAbbreviations.put("tv", "TV Show");
        movieAbbreviations.put("tvs", "TV Series");
        movieAbbreviations.put("tvmini", "TV Miniseries");
        movieAbbreviations.put("biop","Biographical Picture");
        movieAbbreviations.put("noir","Black");


        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        try{
            String query = "select * from genres;";
            // Declare our statement
            PreparedStatement genre_statement = connection.prepareStatement(query);

            ResultSet rs_genres = genre_statement.executeQuery();


            while (rs_genres.next()){
                String genre_id = rs_genres.getString("id");
                String genre_name = rs_genres.getString("name");

                mysqlGenreData.put(genre_name,genre_id);

            }
            genre_statement.close();
            rs_genres.close();
            connection.close();

        }
        catch(Exception e){

        }

    }


    private void insertMovie() throws Exception {
        hashMovieCreate();
        System.out.println(movies.size());
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        PreparedStatement movieInsertPS = null;
        String movieInsertSQL = null;
        movieInsertSQL = "call add_moviexml(?,?,?);";
        try{
            connection.setAutoCommit(false);
            movieInsertPS = connection.prepareStatement(movieInsertSQL);
            for (Movie movie: movies){
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
                    System.out.println("inconsistency: movie year field (<year>) cannot be empty: "+movie.getYear());
                }
                else{
                    System.out.println(movie.getTitle());
                    System.out.println(movie.getYear());
                    System.out.println(movie.getDirName());

                    movieInsertPS.setString(1,movie.getTitle());
                    movieInsertPS.setString(2,String.valueOf(movie.getYear()));
                    movieInsertPS.setString(3,movie.getDirName());
                    movieInsertPS.addBatch();

                }

            }
            movieInsertPS.executeBatch();
            connection.commit();
        } catch (Exception e){
            System.out.println(e);
        }
        try{
            if (movieInsertPS!=null) movieInsertPS.close();
            if (connection!=null) connection.close();
        } catch (Exception e){
            System.out.println(e);
        }
        connection.close();
        hashMovieCreate();

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
            connection.close();

        }
        catch(Exception e){

        }
//        connection.close();

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
        try {
            connection.setAutoCommit(false);
            actorInsertPS = connection.prepareStatement(actorInsertSQL);
            for (Director director : directors) {
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
//        connection.close();

    }

    private void insertGenreInMovies() throws Exception {
        hashMovieCreate();
        hashGenreCreate();

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        PreparedStatement genreInsertPS = null;
        String genreInsertSQL = null;
        genreInsertSQL = "call insert_genreInMoviesxml(?,?);";

        try {
            connection.setAutoCommit(false);
            genreInsertPS = connection.prepareStatement(genreInsertSQL);
            for (Movie movie : movies) {
                String movie_key = movie.getTitle()+","+movie.getDirName();
                movie_key = movie_key.toLowerCase();
                System.out.println(movie_key);
                if (mysqlMovieData2.containsKey(movie_key)) {

                    String[] genres = movie.getGenres().split(";");
                    for (int i = 0; i < genres.length; i++) {
                        String currentGenre = genres[i].toLowerCase();

                        if (currentGenre == ""){
                            System.out.println("inconsistency: genre given in (<cat>) is null: " + currentGenre);
                        }
                        else if (!movieAbbreviations.containsKey(currentGenre)){
                            System.out.println("inconsistency: genre given (<cat>) is not part of DND movie category: " + currentGenre);
                        }
                        else if (!mysqlGenreData.containsKey(movieAbbreviations.get(currentGenre))){
                            System.out.println("inconsistency: does not exists: " + movieAbbreviations.get(currentGenre));
                        }
                        else{
                            genreInsertPS.setString(1, mysqlGenreData.get(movieAbbreviations.get(currentGenre)));
                            genreInsertPS.setString(2, mysqlMovieData2.get(movie_key));
                            genreInsertPS.addBatch();
                        }


                    }
                }
                else{
                    System.out.println("inconsistency: movie does not exist in database, unable to add: "+movie.getTitle());

                }
            }
            genreInsertPS.executeBatch();
            connection.commit();
        } catch (Exception e){
            System.out.println(e);
        }
        try{
            if (genreInsertPS!=null) genreInsertPS.close();
            if (connection!=null) connection.close();
        } catch (Exception e){
            System.out.println(e);
        }

        connection.close();


    }



    public static void main(String[] args) throws Exception {
        // create an instance

//        PrintStream o = new PrintStream(new File("A.txt"));
//
//        // Store current System.out before assigning a new value
//        PrintStream console = System.out;
//        // Assign o to output stream
//        System.setOut(o);

        DomParserExample domParse = new DomParserExample();

        // call run example
        domParse.run("actors63.xml", "actor");
//        domParse.run("casts124.xml", "dirfilms");
//        domParse.run("mains243.xml", "directorfilms");
        domParse.insertStars();
//        domParse.insertMovie();
//        domParse.insertGenre();
//        domParse.insertActorsInMovies();
//        domParse.insertGenreInMovies();


    }

}