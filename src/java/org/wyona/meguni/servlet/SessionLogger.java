package org.wyona.meguni.servlet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Category;

/**
 *
 */
public class SessionLogger implements HttpSessionListener {
    private Category log = Category.getInstance(SessionLogger.class);

    /**
     *
     */
    public void sessionCreated(HttpSessionEvent event) {
        log.error("Session has been created ...");
    }

    /**
     *
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        log.error("Session has been destroyed ...");
    }
}
