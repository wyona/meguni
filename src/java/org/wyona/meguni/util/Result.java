package org.wyona.meguni.util;

import java.net.URL;

/**
 *
 */
public class Result {
    public String title;
    public String excerpt;
    public URL url;
    public String lastModified;

    /**
     *
     */
    public Result(String title, String excerpt, URL url) {
        this.title = title;
        this.excerpt = excerpt;
        this.url = url;
        this.lastModified = null;
    }

    /**
     *
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     *
     */
    public String toString() {
        return "title: " + title + "\nexcerpt: " + excerpt + "\nurl: " + url + "\nlast-modified: " + lastModified;
    }
}
