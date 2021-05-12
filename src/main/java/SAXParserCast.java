
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


    List<Movie> myMovie;
    List<Actor> myActor;

    private String tempVal;

    private boolean inDirectorFilms;
    //to maintain context
    private Actor tempActor;
    private Movie tempMovie;

    private String dirName;
    private String dirId;
    private String actorName;

    public HashMap<String,String> mysqlActorData = new HashMap<>();
    public HashMap<String,String> mysqlMovieData = new HashMap<>();



    public SAXParserCast() {
        myMovie = new ArrayList<Movie>();
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

//        System.out.println("No of Actors '" + myMovie.size() + "'.");
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
        if (qName.equalsIgnoreCase("dirfilms")) {
            inDirectorFilms = true;
            //create a new instance of actor
        }
        if (qName.equalsIgnoreCase("filmc")) {
            tempMovie = new Movie();
            tempMovie.setDir_id(dirId);
            tempMovie.setDir_name(dirName);

        }
        if (qName.equalsIgnoreCase("m")) {
            tempActor = new Actor();
            tempActor.setName(actorName);
            tempMovie = new Movie();
            tempMovie.setDir_id(dirId);
            tempMovie.setDir_name(dirName);

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("dirfilms")) {
            inDirectorFilms = false;
        }else if(qName.equalsIgnoreCase("is")){
            dirName = tempVal;
        }
        else if(qName.equalsIgnoreCase("dirid")){
            dirId =tempVal;
        }
        else if (qName.equalsIgnoreCase("f")) {
            System.out.println(tempVal);
            tempMovie.setF(tempVal);
        }
        else if (qName.equalsIgnoreCase("t")) {
            System.out.println(tempVal);
            tempMovie.setTitle(tempVal);
        }

        else if (qName.equalsIgnoreCase("m")) {
            myMovie.add(tempMovie);

        }
        else if (qName.equalsIgnoreCase("a")){
            actorName = tempVal;
            System.out.println(tempVal);
            System.out.println();
        }

    }



    public static void main(String[] args) throws Exception {
        SAXParserCast spa = new SAXParserCast();
        spa.runExample();
    }

}
