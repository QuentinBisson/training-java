package fr.ebiz.computerdatabase.persistence.factory;

import fr.ebiz.computerdatabase.persistence.exception.DaoConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DaoConfiguration {
    private static final String URL_PROPERTY = "url";
    private static final String DRIVER_PROPERTY = "driver";
    private static final String USERNAME_PROPERTY = "username";
    private static final String PASSWORD_PROPERTY = "password";

    private final String url;
    private final String driver;
    private final String username;
    private final String password;

    public DaoConfiguration(String propertyFile) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream propertyFileStream = classLoader.getResourceAsStream(propertyFile);

            if (propertyFileStream == null) {
                throw new DaoConfigurationException("Property file" + propertyFile + " not found.");
            }

            Properties properties = new Properties();
            properties.load(propertyFileStream);
            url = properties.getProperty(URL_PROPERTY);
            driver = properties.getProperty(DRIVER_PROPERTY);
            username = properties.getProperty(USERNAME_PROPERTY);
            password = properties.getProperty(PASSWORD_PROPERTY);
        } catch (IOException e) {
            throw new DaoConfigurationException("Can't load property file " + propertyFile, e);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }

}
