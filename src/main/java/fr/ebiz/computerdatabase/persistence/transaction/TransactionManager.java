package fr.ebiz.computerdatabase.persistence.transaction;

import java.sql.Connection;

public interface TransactionManager {

    /**
     * Get the current connection from the ThreadLocal.
     *
     * @return The connection
     */
    Connection getConnection();

    /**
     * Open a connection and if needed a transaction context.
     *
     * @param openTransaction Open a transation context ?
     */
    void open(boolean openTransaction);

    /**
     * Commit the transaction if needed.
     */
    void commit();

    /**
     * Rollback the transaction if something came up.
     */
    void rollback();

    /**
     * Close the transaction.
     */
    void close();
}
