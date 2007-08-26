package org.wyona.meguni.parser.impl;

import org.wyona.meguni.parser.Parser;
import org.wyona.meguni.util.Result;
import org.wyona.meguni.util.ResultSet;

import org.apache.log4j.Category;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 */
public class GoogleParser extends Parser {
    private Category log = Category.getInstance(GoogleParser.class);

    private String sampleFilename = "google.html";

    /**
     *
     */
    public ResultSet parse(String queryString) throws Exception {
        log.debug("Parse: " + queryString);

        ResultSet resultSet = new ResultSet("Google", "http://www.google.com");

        URL searchURL;
        try {
            //searchURL = new URL("http://www.google.com/search?q=" + queryString);
            searchURL = new URL("http://www.google.com/search?hl=en&q=" + queryString + "&btnG=Google+Search");
        } catch (MalformedURLException e) {
            log.error(e.toString());
            return null;
        }

        log.info("Connect to " + searchURL);

        // Connect and parse Input Stream
        java.io.InputStream in = null;
        try {
            URLConnection uc = searchURL.openConnection();
            uc.setRequestProperty("User-Agent", "Mozilla/5.0");
            uc.connect();
            in = uc.getInputStream();
        } catch (UnknownHostException e) {
            File sampleFile = new File(getSamplesDir(), sampleFilename);
            log.warn(e.toString() + " ---  Read sample file: " + sampleFile);
            in = new java.io.FileInputStream(sampleFile);
        }

        try {
            // To debug
            //java.io.FileOutputStream out = new java.io.FileOutputStream(sampleFilename);



            //String marker = "<html>";
            String marker = "<div class=g>";
            //String marker = "<p class=\"g\">";
            int markerLength = marker.length();
            int c = -1;
            int numberOfCharsMatched = 0;
            int position = 0;
            StringBuffer sb = new StringBuffer("");
            while ((c = in.read()) != -1) {
                //out.write(c);
                position++;
                //log.error("Character: " + (char)c);
                if (c == marker.charAt(numberOfCharsMatched)) {
                    numberOfCharsMatched++;
                    sb.append((char)c);
                    if (numberOfCharsMatched == markerLength) {
                        log.debug("Marker " + marker + " found at position: " + (position - markerLength));
                        numberOfCharsMatched = 0;
                        sb = new StringBuffer("");

                        String titleWithLink = searchEnd(in, position, "</a>");
                        //String titleWithLink = searchEnd(in, position, "<br>");
                        position = position + titleWithLink.length();
                        if (titleWithLink != null) {
                            log.debug("Title and Link: " + titleWithLink);
                            String link = titleWithLink.substring(titleWithLink.indexOf("<a") + 9); // Beginning of Link
                            String title = link.substring(link.indexOf(">") + 1, link.indexOf("</a>"));
                            log.debug("Title: " + title);

                            log.debug("Not Link yet: " + link);
                            link = link.substring(0, link.indexOf("\"")); // End of Link
                            log.debug("Link: " + link);

                            // TODO: Something is wrong with retrieving the excerpt!
                            String excerpt = "Excerpt not implemented yet!";
/*
			    String excerpt = searchEnd(in, position, "color=#008000");
                            position = position + excerpt.length();

			    try {
                                excerpt = excerpt.substring(excerpt.indexOf("<font") + 14, excerpt.length() - 23);
                                log.debug("Excerpt: " + excerpt);
                            } catch (StringIndexOutOfBoundsException e) {
                                excerpt = "No excerpt!";
                                log.warn("No excerpt!");
                            }
*/

                            resultSet.add(new Result(title, excerpt, new URL(link)));
                        } else {
                            log.error("No end of search result found!");
                            //position = position + ???;
                        }
                    }
                } else {
                    if (numberOfCharsMatched > 0) {
                        //log.error("Sorry, no marker " + sb.toString()  + " at position: " + (position - numberOfCharsMatched));
                        numberOfCharsMatched = 0;
                        sb = new StringBuffer("");
                    }
                }
            }
            in.close();

            //out.close();
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
        return resultSet;
    }

    /**
     * Search for marker representing the end of an element (e.g. title, excerpt, ...)
     */
    private String searchEnd(InputStream in, int position, String marker) throws IOException {
        log.debug("Search marker: " + marker);
        int markerLength = marker.length();
        int numberOfCharsMatched = 0;
        StringBuffer sb = new StringBuffer("");
        int c = -1;
        while ((c = in.read()) != -1) {
            position++;
            sb.append((char)c);
            if (c == marker.charAt(numberOfCharsMatched)) {
                numberOfCharsMatched++;
                if (numberOfCharsMatched == markerLength) {
                    log.debug("Marker " + marker + " found at position: " + (position - markerLength));
                    return sb.toString();
                }
            } else {
                if (numberOfCharsMatched > 0) {
                    //log.debug("Sorry, no marker " + sb.toString()  + " at position: " + (position - numberOfCharsMatched));
                    numberOfCharsMatched = 0;
                }
            }
        }
        return null;
    }
}
