package fr.ebiz.computerdatabase.persistence.factory;

import fr.ebiz.computerdatabase.persistence.exception.DaoConfigurationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DaoFactory {

    private static final String CONFIGURATION_FILE_PROPERTY = "db.properties";

    private static DaoFactory instance;

    private DaoConfiguration configuration;
    
    private DaoFactory(DaoConfiguration configuration) {
        this.configuration = configuration;
    }

    public synchronized static DaoFactory getInstance() {
        if (instance == null) {
            DaoConfiguration configuration = new DaoConfiguration(CONFIGURATION_FILE_PROPERTY);

            try {
                Class.forName(configuration.getDriver());
            } catch (ClassNotFoundException e) {
                throw new DaoConfigurationException("Driver not found in classpath.", e);
            }

            instance = new DaoFactory(configuration);
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
        } catch (SQLException e) {
            throw new DaoConfigurationException("Could not open connection", e);
        }
    }

}
