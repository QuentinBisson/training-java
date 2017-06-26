package fr.ebiz.computerdatabase.persistence.factory;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.Dao;
import fr.ebiz.computerdatabase.persistence.dao.impl.CompanyDaoImpl;
import fr.ebiz.computerdatabase.persistence.dao.impl.ComputerDaoImpl;
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

    public static DaoFactory getInstance() {
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

    public final Dao<?, ?> make(Class<?> clazz) {
        if (Company.class.equals(clazz)) {
            return new CompanyDaoImpl(getInstance());
        } else if(Computer.class.equals(clazz)) {
            return new ComputerDaoImpl(getInstance());
        }
        throw new IllegalArgumentException("No dao exists for this class");
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
        } catch (SQLException e) {
            throw new DaoConfigurationException("Could not open connection", e);
        }
    }
}
