package fr.ebiz.computerdatabase.persistence.transaction.impl;


import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.factory.ConnectionPool;
import fr.ebiz.computerdatabase.persistence.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManagerImpl extends ThreadLocal<Connection> implements TransactionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManagerImpl.class.getName());
    private final DataSource dataSource;

    /**
     * Constructor.
     *
     * @param dataSource The injected dataSource
     */
    private TransactionManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static TransactionManager getInstance() {
        return Transaction.INSTANCE.getTransactionManager();
    }

    @Override
    public Connection getConnection() {
        return this.get();
    }

    @Override
    public void commit() {
        try {
            getConnection().commit();
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
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(!openTransaction);
            set(connection);
        } catch (SQLException e) {
            close();
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Could not open transaction", e);
        }
    }

    @Override
    public void close() {
        try {
            Connection connection = getConnection();
            connection.setAutoCommit(true);
            connection.close();

            // Clean the thread local
            remove();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Could not close transaction", e);
        }
    }

    /*
        *  This pool is made for short quick transactions that the web application uses.
        *  Using enum singleton pattern for lazy singletons
        */
    private enum Transaction {
        INSTANCE(new TransactionManagerImpl(ConnectionPool.getInstance()));
        private final TransactionManager local;

        /**
         * Constructor.
         *
         * @param transactionManager The unique transactionManager
         */
        Transaction(TransactionManager transactionManager) {
            this.local = transactionManager;
        }

        public TransactionManager getTransactionManager() {
            return local;
        }
    }
}
