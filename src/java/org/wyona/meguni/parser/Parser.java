package org.wyona.meguni.parser;

import org.wyona.meguni.util.FileUtil;
import org.wyona.meguni.util.ResultSet;

import org.apache.log4j.Category;

import java.io.File;
import java.net.URL;
import java.net.URI;
import java.util.Properties;

/**
 *
 */
public abstract class Parser {
    private Category log = Category.getInstance(Parser.class);

    private File samplesDir = null;

    /**
     *
     */
    public abstract ResultSet parse(String queryString) throws Exception;

    /**
     *
     */
    private void getProperties() {
        URL propertiesURL = Parser.class.getClassLoader().getResource("meguni.properties");

        log.debug("Properties file: " + propertiesURL);

        Properties props = new Properties();
        try {
            props.load(propertiesURL.openStream());
        } catch (Exception e) {
            log.error("Error reading resource from URL [" + propertiesURL + "]", e);
        }

        try {
            File propsFile = new File(new URI(propertiesURL.toString()));
            samplesDir = new File(props.getProperty("samples.dir"));
            if (!samplesDir.isAbsolute()) {
                //log.debug(propsFile);
                //log.debug(samplesDir);
                samplesDir = FileUtil.file(propsFile.getParent(), samplesDir.toString());
                //samplesFile = new File(propsFile, samplesFile.toString());
            }
            log.debug("Samples directory: " + samplesDir);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     *
     */
    protected File getSamplesDir() {
        getProperties();
        return samplesDir;
    }
}
