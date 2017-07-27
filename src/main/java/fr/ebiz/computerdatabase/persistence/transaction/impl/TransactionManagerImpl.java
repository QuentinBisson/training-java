package fr.ebiz.computerdatabase.persistence.transaction.impl;


import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class TransactionManagerImpl implements TransactionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagerImpl.class.getName());

    private final DataSource dataSource;
    private final ThreadLocal<Connection> threadLocal;

    /**
     * Constructor.
     *
     * @param dataSource The datasource
     */
    @Autowired
    private TransactionManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.threadLocal = new ThreadLocal<>();
    }

    @Override
    public Connection getConnection() {
        return threadLocal.get();
    }

    @Override
    public void commit() {
        try {
            Connection connection = threadLocal.get();
            if (connection != null) {
                connection.commit();
            }
        } catch (SQLException e) {
            rollback();
            close();
            throw new DaoException("Could not commit transaction. Transaction was rollbacked !", e);
        }
    }

    @Override
    public void rollback() {
        try {
            getConnection().rollback();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            close();
            throw new DaoException("Could not rollback transaction !", e);
        }
    }

    @Override
    public void open(boolean openTransaction) {
        try {
            Connection connection = threadLocal.get();
            if (connection == null) {
                connection = dataSource.getConnection();
                threadLocal.set(connection);
                connection.setAutoCommit(!openTransaction);
            }
        } catch (SQLException e) {
            close();
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Could not open transaction", e);
        }
    }

    @Override
    public void close() {
        try {

            Connection connection = threadLocal.get();
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
            // Clean the thread local
            threadLocal.remove();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Could not close transaction", e);
        }
    }
}
