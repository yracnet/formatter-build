/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.yracnet.formatter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.revelc.code.formatter.FormatterException;
import net.revelc.code.formatter.model.ConfigReadException;
import net.revelc.code.formatter.model.ConfigReader;
import org.xml.sax.SAXException;

/**
 *
 * @author wyujra
 */
public class FormatterHelp {

    public static final String CACHE_PROPERTIES_FILENAME = "formatter-cache.properties";
    public static final String[] DEFAULT_INCLUDES = new String[]{"**/*.java", "**/*.js", "**/*.html", "**/*.xml", "**/*.json", "**/*.css"};

    //private static FormatterLog logger;
    /**
     * Read config file and return the config as {@link Map}.
     *
     * @param newConfigFile
     * @param basedir
     * @return the options from config file
     * @throws FormatterException the mojo execution exception
     */
    public static Map<String, String> getOptionsFromConfigFile(String newConfigFile, File basedir) throws FormatterException {
        try ( InputStream configInput = searchFile(newConfigFile, basedir)) {
            return new ConfigReader().read(configInput);
        } catch (IOException e) {
            throw new FormatterException("Cannot read config file [" + newConfigFile + "]", e);
        } catch (SAXException e) {
            throw new FormatterException("Cannot parse config file [" + newConfigFile + "]", e);
        } catch (ConfigReadException e) {
            throw new FormatterException(e.getMessage(), e);
        }
    }

    /**
     * Read properties file and return the properties as {@link Map}.
     *
     * @param newPropertiesFile
     * @param basedir
     * @return the options from properties file or null if not properties file
     * found
     * @throws FormatterException the mojo execution exception
     */
    public static Map<String, String> getOptionsFromPropertiesFile(String newPropertiesFile, File basedir) throws FormatterException {
        Properties properties = new Properties();
        try {
            InputStream propertiesInput = searchFile(newPropertiesFile, basedir);
            properties.load(propertiesInput);
        } catch (IOException e) {
            throw new FormatterException("Cannot read config file [" + newPropertiesFile + "]", e);
        }
        final Map<String, String> map = new HashMap<>();
        for (final String name : properties.stringPropertyNames()) {
            map.put(name, properties.getProperty(name));
        }
        return map;
    }

    /**
     * Store file hash cache.
     *
     * @param props the props
     * @param targetDirectory
     */
    public static void storeFileHashCache(Properties props, File targetDirectory) {
        File cacheFile = new File(targetDirectory, FormatterHelp.CACHE_PROPERTIES_FILENAME);
        try ( OutputStream out = new BufferedOutputStream(new FileOutputStream(cacheFile))) {
            props.store(out, null);
        } catch (IOException e) {
            //logger.warn("Cannot store file hash cache properties file", e);
        }
    }

    /**
     * Search file in directory or in ClassPath
     *
     * @param name
     * @param directory
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static InputStream searchFile(String name, File directory) throws FileNotFoundException {
        File file = new File(directory, name);
        if (!file.exists()) {
            if (!name.startsWith("/")) {
                name = "/" + name;
            }
            return FormatterHelp.class.getResourceAsStream(name);
        }
        return new FileInputStream(file);
    }

    /**
     * Read file hash cache file.
     *
     * @param targetDirectory
     * @return the properties
     */
    public static Properties readFileHashCacheFile(File targetDirectory) {
        Properties props = new Properties();
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        } else if (!targetDirectory.isDirectory()) {
            //logger.warn("Something strange here as the '" + targetDirectory.getPath()
            //        + "' supposedly target directory is not a directory.");
            return props;
        }
        File cacheFile = new File(targetDirectory, FormatterHelp.CACHE_PROPERTIES_FILENAME);
        if (!cacheFile.exists()) {
            return props;
        }
        try ( BufferedInputStream stream = new BufferedInputStream(new FileInputStream(cacheFile))) {
            props.load(stream);
        } catch (IOException e) {
            //logger.warn("Cannot load file hash cache properties file", e);
        }
        return props;
    }
}
