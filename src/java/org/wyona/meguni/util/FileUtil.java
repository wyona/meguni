package org.wyona.meguni.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.apache.log4j.Category;

/**
 *
 */
public final class FileUtil {
    private static Category log = Category.getInstance(FileUtil.class);

    /**
     * Returns a file by specifying an absolute directory name and a relative file name
     * 
     * @param absoluteDir DOCUMENT ME!
     * @param relativeFile DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static File file(String absoluteDir, String relativeFile) {
        File file = new File(fileName(absoluteDir, relativeFile));
        return file;
    }

    /**
     * Returns an absolute file name by specifying an absolute directory name and a relative file
     * name
     * 
     * @param absoluteFile DOCUMENT ME!
     * @param relativeFile DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static String concat(String absoluteFile, String relativeFile) {
        File file = new File(absoluteFile);

        if (file.isFile()) {
            return fileName(file.getParent(), relativeFile);
        }

        return fileName(absoluteFile, relativeFile);
    }

    /**
     * Returns an absolute file name by specifying an absolute directory name and a relative file
     * name
     * 
     * @param absoluteDir DOCUMENT ME!
     * @param relativeFile DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private static String fileName(String absoluteDir, String relativeFile) {
        String fileName = null;
        String newAbsoluteDir = null;

        if (!(absoluteDir.charAt(absoluteDir.length() - 1) == '/')) {
            newAbsoluteDir = absoluteDir + "/";
        } else {
            newAbsoluteDir = absoluteDir;
        }

        if (relativeFile.indexOf("../") == 0) {
            StringTokenizer token = new StringTokenizer(newAbsoluteDir, "/");
            newAbsoluteDir = "/";

            int numberOfTokens = token.countTokens();

            for (int i = 0; i < (numberOfTokens - 1); i++) {
                newAbsoluteDir = newAbsoluteDir + token.nextToken() + "/";
            }

            String newRelativeFile = relativeFile.substring(3, relativeFile.length());
            fileName = fileName(newAbsoluteDir, newRelativeFile);
        } else if (relativeFile.indexOf("./") == 0) {
            fileName = newAbsoluteDir + relativeFile.substring(2, relativeFile.length());
        } else {
            fileName = newAbsoluteDir + relativeFile;
        }

        return fileName;
    }
}
