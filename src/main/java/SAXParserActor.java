

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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


public class SAXParserActor extends DefaultHandler {

    List<Actor> myActor;

    private String tempVal;

    //to maintain context
    private Actor tempActor;

    public HashMap<String,String> mysqlActorData = new HashMap<>();


    public SAXParserActor() {
        myActor = new ArrayList<Actor>();
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
            sp.parse("actors63.xml", this);

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

//        System.out.println("No of Employees '" + myActor.size() + "'.");
//
//        Iterator<Actor> it = myActor.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            tempActor = new Actor();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            myActor.add(tempActor);

        } else if (qName.equalsIgnoreCase("stagename")) {
            tempActor.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            int dob = -1;

            if (tempVal !="") {
                try {
                    dob = Integer.parseInt(tempVal);

                } catch (Exception e){

                }
                tempActor.setDob(dob);
            }


        }



    }

    private void insertStars() throws Exception{
        hashActorCreate();
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        try{

            for (Actor actor : myActor){
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
//            System.out.println(e);
        }
        connection.close();
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

    public static void main(String[] args) throws Exception {
        PrintStream o = new PrintStream(new File("A.txt"));
        PrintStream console = System.out;
        System.setOut(o);
        SAXParserActor spa = new SAXParserActor();
        spa.runExample();
        spa.printData();
//        spa.insertStars();
    }

}
