package org.wyona.meguni.util;

import java.util.Vector;

/**
 *
 */
public class ResultSet {
    private Vector results;
    private String sourceName;
    private String sourceDomain;

    /**
     *
     */
    public ResultSet () {
        results = new Vector();
    }

    /**
     *
     */
    public ResultSet(String sourceName, String sourceDomain) {
        this();
        this.sourceName = sourceName;
        this.sourceDomain = sourceDomain;
    }

    /**
     *
     */
    public void add(Result result) {
        results.add(result);
    }

    /**
     *
     */
    public Result get(int i) {
        return (Result) results.elementAt(i);
    }

    /**
     *
     */
    public int size() {
        return results.size();
    }

    /**
     *
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     *
     */
    public String getSourceDomain() {
        return sourceDomain;
    }
}
