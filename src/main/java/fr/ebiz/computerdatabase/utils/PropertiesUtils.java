package fr.ebiz.computerdatabase.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtils {

    /**
     * Get an int from properties.
     *
     * @param properties   The properties object
     * @param propertyName The property name
     * @return The int property
     */
    public static Integer getIntProperty(Properties properties, String propertyName) {
        String property = properties.getProperty(propertyName);
        return Integer.parseInt(property);
    }

    /**
     * Get a long from properties.
     *
     * @param properties   The properties object
     * @param propertyName The property name
     * @return The long property
     */
    public static Long getLongProperty(Properties properties, String propertyName) {
        return Long.parseLong(properties.getProperty(propertyName));
    }


    /**
     * Get a boolean from properties.
     *
     * @param properties   The properties object
     * @param propertyName The property name
     * @return The property
     */
    public static Boolean getBooleanProperty(Properties properties, String propertyName) {
        String property = properties.getProperty(propertyName);
        return new Boolean(property);
    }

    /**
     * Load a property file with context class loader.
     *
     * @param propertyFile Path to the property file
     * @return The loaded properties
     * @throws IOException If an error occurs
     */
    public static Properties loadProperties(String propertyFile) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream propertyFileStream = classLoader.getResourceAsStream(propertyFile);
        Properties properties = new Properties();
        properties.load(propertyFileStream);
        return properties;
    }
}