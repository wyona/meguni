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
 * Maybe it would make sense to use the Rome, Atom/RSS Java utilities
 * or http://jakarta.apache.org/commons/feedparser/
 */
public class NutchParser extends Parser {
    private Category log = Category.getInstance(NutchParser.class);

    private String sampleFilename = "nutch.xml";

    /**
     *
     */
    public ResultSet parse(String queryString) throws Exception {
        log.error("Parse: " + queryString);

        ResultSet resultSet = new ResultSet("Nutch", "http://incubator.apache.org/nutch/");

        URL searchURL;
        try {
	    searchURL = new URL("http://127.0.0.1:8080/opensearch?query=" + queryString);
        } catch (MalformedURLException e) {
            log.error(e.toString());
            return null;
        }

        log.warn("Connect to " + searchURL);

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
            //java.io.FileOutputStream out = new java.io.FileOutputStream("/home/michi/msn.xml");



            String marker = "<item>";
            //String marker = "<li class=\"first\">";
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
                        log.error("Marker " + marker + " found at position: " + (position - markerLength));
                        numberOfCharsMatched = 0;
                        sb = new StringBuffer("");

			String searchResult = searchEndOfResult(in, position);
                        if (searchResult != null) {
                            log.debug("Search result: " + searchResult);
                            resultSet.add(parseResultString(searchResult));
                            //resultSet.add(new Result(searchResult, null, null));
                            position = position + searchResult.length();
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
        } catch (UnknownHostException e) {
            log.error(e.toString());
            return null;
        }
        return resultSet;
    }

    /**
     * NOTE: Using "Cached page" as marker might be a problem if the query string would contain "Cached page"
     */
    private String searchEndOfResult(InputStream in, int position) throws IOException {
        String marker = "</item>";
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
                    log.error("Marker " + marker + " found at position: " + (position - markerLength));
                    return sb.toString();
                }
            } else {
                if (numberOfCharsMatched > 0) {
                    //log.error("Sorry, no marker " + sb.toString()  + " at position: " + (position - numberOfCharsMatched));
                    numberOfCharsMatched = 0;
                }
            }
        }
        return null;
    }

    /**
     *
     */
    private Result parseResultString(String searchResultString) {
        Result result;
        String title = searchResultString.substring(searchResultString.indexOf("<title>") + 7, searchResultString.indexOf("</title>"));
        String excerpt = searchResultString.substring(searchResultString.indexOf("<description>") + 13, searchResultString.indexOf("</description>"));
        String urlString = searchResultString.substring(searchResultString.indexOf("<link>") + 6, searchResultString.indexOf("</link>"));
        String lastModified = null; //searchResultString.substring(searchResultString.indexOf("<pubDate>") + 9, searchResultString.indexOf("</pubDate>"));
        try {
            URL url = new URL(urlString);
            result = new Result(title, excerpt, url);
	    result.setLastModified(lastModified);
        } catch (Exception e) {
            log.warn(e.toString());
            result = new Result(title, excerpt, null);
        }
        log.error(result.toString());

        return result;
    }
}
