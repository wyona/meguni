package org.wyona.meguni.servlet;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.wyona.meguni.parser.Parser;
import org.wyona.meguni.util.Result;
import org.wyona.meguni.util.ResultSet;

import org.apache.log4j.Category;

/**
 *
 */
public class SearchServlet extends HttpServlet {
    private Category log = Category.getInstance(SearchServlet.class);

    /**
     *
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        String queryString = request.getParameter("qs");
        if (queryString == null) {
            queryString = "";
        }

        String format = request.getParameter("format");
        if (format == null) {
            format = "html";
        }

        String[] searchEngines = request.getParameterValues("search-engine");
        if (searchEngines == null) {
            searchEngines = new String[0];
        }

	boolean nutchChecked = false;
	boolean googleChecked = false;
	boolean msnChecked = false;
        ResultSet[] resultSets = new ResultSet[searchEngines.length];
        for (int i = 0;i < searchEngines.length; i++) {
            //writer.println("Try Search Engine: " + searchEngines[i] + " ...<br>");
            if (searchEngines[i].equals("Nutch")) nutchChecked = true;
            if (searchEngines[i].equals("Google")) googleChecked = true;
            if (searchEngines[i].equals("MSN")) msnChecked = true;
            try {
                resultSets[i] = getSearchResults(searchEngines[i], queryString);
            } catch (Exception e) {
                log.error(e);
            }
        }

        StringBuffer sb = new StringBuffer("");
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<meguni xmlns=\"http://www.meguni.com/search-and-results/1.0\">");
        sb.append("<query-string>" + queryString + "</query-string>");
        sb.append("<results>");
        for (int i = 0;i < resultSets.length; i++) {
            ResultSet resultSet = resultSets[i];
            if (resultSet != null && resultSet.size() > 0) {
                sb.append("<provider source-name=\"" + resultSet.getSourceName() + "\" source-domain=\"" + resultSet.getSourceDomain() + "\" numberOfResults=\""+resultSet.size()+"\">");
                for (int k = 0;k < resultSet.size(); k++) {
                    sb.append("<result number=\"" + (k+1) + "\" source-name=\"" + resultSet.getSourceName() + "\">");
                    sb.append("<title><![CDATA[" + resultSet.get(k).title + "]]></title>");
                    sb.append("<excerpt><![CDATA[" + resultSet.get(k).excerpt + "]]></excerpt>");
                    sb.append("<url><![CDATA[" + resultSet.get(k).url + "]]></url>");
                    sb.append("<last-modified><![CDATA[" + resultSet.get(k).lastModified + "]]></last-modified>");
                    sb.append("</result>");
                }
                sb.append("</provider>");
            } else {
                sb.append("<no-results source=\"" + searchEngines[i] + "\"/>");
            }
        }
        sb.append("</results>");
        sb.append("</meguni>");

        PrintWriter writer = response.getWriter();
        if (format.equals("html")) {
            response.setContentType("text/html");
            writer.println("<html>");
            writer.println("<link rel=\"stylesheet\" href=\"css/global.css\" type=\"text/css\">");
            writer.println("<link rel=\"stylesheet\" href=\"css/results.css\" type=\"text/css\">");
            writer.println("<body>");


/*
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            writer.println("Name: " + name + "<br>");
        }
*/


            writer.println("<table id=\"head\"><tr>");
            writer.println("<td id=\"small-logo\"><a href=\"index.html\">MEGUNI</a></td>");
            writer.println("<td id=\"search-form\">");
            writer.println("<form action=\"search-and-results\" method=\"GET\">");
            writer.println("<input type=\"text\" name=\"qs\" size=\"45\" value=\"" + queryString + "\"/>&#160;<input type=\"submit\" value=\"Search\"/>");
            writer.println("<br>");
            if (nutchChecked) {
                writer.println("<input type=\"checkbox\" name=\"search-engine\" value=\"Nutch\" checked/>Nutch");
            } else {
                writer.println("<input type=\"checkbox\" name=\"search-engine\" value=\"Nutch\"/>Nutch");
            }
            if (googleChecked) {
                writer.println("<input type=\"checkbox\" name=\"search-engine\" value=\"Google\" checked/>Google");
            } else {
                writer.println("<input type=\"checkbox\" name=\"search-engine\" value=\"Google\"/>Google");
            }
            if (msnChecked) {
                writer.println("<input type=\"checkbox\" name=\"search-engine\" value=\"MSN\" checked/>MSN");
            } else {
                writer.println("<input type=\"checkbox\" name=\"search-engine\" value=\"MSN\"/>MSN");
            }
            writer.println("</form>");
            writer.println("</td>");
            writer.println("</tr></table>");

            writer.println("<div id=\"results\">");
            for(int j = 0; j < 10; j++) {
            for (int i = 0;i < resultSets.length; i++) {
                ResultSet resultSet = resultSets[i];
                if (resultSet != null && resultSet.size() > j) {
                    //writer.println("Results of " + searchEngines[i] + ": Number of results " + resultSet.size()  + " <br>");
                    //for (int k = 0;k < resultSet.size(); k++) {
                        Result result = resultSet.get(j);
                        writer.println("<div class=\"item\">");
                        writer.println("<div class=\"item-title\"><a href=\"" + result.url + "\">" + result.title + "</a></div>");
                        writer.println("<div class=\"item-excerpt\">" + result.excerpt + "</div>");
                        writer.println("<div class=\"item-url\">");
                        writer.println("<span class=\"item-url-remember\"><a href=\"remember.html?url=" + result.url + "\">Remember</a></span>");
                        writer.println("<span class=\"item-url-url\">" + result.url + "</span>");
                        writer.println("<span class=\"item-source\">Found by <a href=\""+resultSet.getSourceDomain()+"\">" + resultSet.getSourceName() + "</a></span>");
                        writer.println("<span class=\"item-number\">(" + (j + 1) + ")</span>");
                        writer.println("</div>");
                        writer.println("</div>");
                    //}
                } else {
                    //writer.println("No search results from: " + searchEngines[i] + "<br>");
                }
            }
            }
            writer.println("</div>");

            writer.println("</body>");
            writer.println("</html>");
        } else if (format.equals("xml")) {
            response.setContentType("application/xml");
            writer.println(sb.toString());
        } else {
            response.setContentType("text/html");
            writer.println("<html>");
            writer.println("<body>");
            writer.println("<p>No such format: " + format + "</p>");
            writer.println("</body>");
            writer.println("</html>");
        }
    }

    /**
     *
     */
/*
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("text/html");

        writer.println("<html>");
        writer.println("<body>");

        String name = request.getParameter("name");
        if (name != null && name.length() > 0) {
           String value = request.getParameter("value");
	   HttpSession session = request.getSession(true);
           session.setAttribute(name, value);

           writer.println("Session attribute has been set: " + name + " = " + value);
        } else {
           writer.println("No session attribute has been set!");
        }

        writer.println("</body>");
        writer.println("</html>");
    }
*/

    /**
     *
     */
    private ResultSet getSearchResults(String name, String queryString) throws Exception {
        String className = "org.wyona.meguni.parser.impl." + name + "Parser";
        Parser parser =  null;
        try {
            parser = (Parser) Class.forName(className).newInstance();
        } catch (ClassNotFoundException e) {
            getServletContext().log("No such class: " + className);
            log.error("No such class: " + className);
            return null;
        } catch (Exception e) {
            getServletContext().log(e.toString());
            log.error(e.toString());
            return null;
        }
        return parser.parse(queryString);
    }
}
