package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.persistence.Dao;
import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T, ID> implements Dao<T, ID> {
    protected static final int FIRST_PARAMETER_INDEX = 1;
    protected static final String INSERT_INTO_QUERY = "INSERT INTO ";
    protected static final String UPDATE_QUERY = "UPDATE ";
    protected static final String WHERE_OPERATOR = " WHERE ";
    protected static final String EQUAL_OPERATOR = " = ";

    private static final String ERROR_WHILE_PREPARING_STATEMENT_EXCEPTION = "Error while preparing statement";
    private static final String TOO_MANY_RESULTS_WERE_FOUND_FOR_ID_EXCEPTION = "Too many results were found for id = ";
    private static final String COULD_NOT_MAP_RESULT_EXCEPTION = "Could not map result";
    private static final String COULD_NOT_CLOSE_CONNECTION_EXCEPTION = "Could not close connection";
    private static final String SELECT_QUERY = "SELECT * FROM ";
    private static final String DELETE_QUERY = "DELETE FROM ";
    private static final String COUNTER_ALIAS = "CNT";
    private static final String COUNT_QUERY = "SELECT COUNT(*) as " + COUNTER_ALIAS + " FROM ";
    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private DaoFactory daoFactory;

    protected AbstractDao(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    protected abstract String getTableName();

    protected abstract String getIDSelector();

    protected abstract String getInsertQuery();

    protected abstract String getUpdateQuery();

    protected abstract void bindID(PreparedStatement statement, ID id) throws SQLException;

    protected abstract void bindModel(PreparedStatement statement, T model, boolean updating) throws SQLException;

    protected abstract T map(ResultSet resultset) throws SQLException;

    protected abstract void mapGeneratedId(ResultSet resultset, T model) throws SQLException;

    @Override
    public Optional<T> get(ID id) throws DaoException {
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY + getTableName() + WHERE_OPERATOR + getIDSelector())) {
                bindID(statement, id);
                List<T> found = executeQuery(statement);
                if (found.isEmpty()) {
                    return Optional.empty();
                } else if (found.size() > 1) {
                    String message = TOO_MANY_RESULTS_WERE_FOUND_FOR_ID_EXCEPTION + id;
                    logger.error(message);
                    throw new DaoException(message);
                }
                return Optional.of(found.get(0));
            } catch (SQLException e) {
                handleStatementPreparationException(e);
            }
        } finally {
            closeConnection(connection);
        }

        return Optional.empty();
    }

    @Override
    public List<T> getAll() throws DaoException {
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY + getTableName())) {
                return executeQuery(statement);
            } catch (SQLException e) {
                handleStatementPreparationException(e);
            }
        } finally {
            closeConnection(connection);
        }
        return Collections.emptyList();
    }

    @Override
    public int count() throws DaoException {
        String queryString = COUNT_QUERY + getTableName();
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(queryString)) {
                try (ResultSet resultSet = statement.executeQuery()) {

                    if (resultSet != null && resultSet.next()) {
                        return resultSet.getInt(COUNTER_ALIAS);
                    }

                } catch (SQLException e) {
                    handleCouldNotMapException(e);
                }
            } catch (SQLException e) {
                handleStatementPreparationException(e);
            }
        } finally {
            closeConnection(connection);
        }

        return 0;
    }

    @Override
    public void insert(T model) throws DaoException {
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(getInsertQuery(), Statement.RETURN_GENERATED_KEYS)) {
                bindModel(statement, model, false);
                int insertedRows = statement.executeUpdate();
                if (insertedRows == 0) {
                    throw new DaoException("Creation failed, no affected rows.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    mapGeneratedId(generatedKeys, model);
                }
            } catch (SQLException e) {
                handleStatementPreparationException(e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public void update(T model) throws DaoException {
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(getUpdateQuery(), Statement.RETURN_GENERATED_KEYS)) {
                bindModel(statement, model, true);

                int updatedRows = statement.executeUpdate();
                if (updatedRows == 0) {
                    throw new DaoException("Update failed, no affected rows.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    mapGeneratedId(generatedKeys, model);
                }
            } catch (SQLException e) {
                handleStatementPreparationException(e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public void delete(ID id) throws DaoException {
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY + getTableName() + WHERE_OPERATOR + getIDSelector())) {
                bindID(statement, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                handleStatementPreparationException(e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    private void handleStatementPreparationException(SQLException e) throws DaoException {
        logger.error(ERROR_WHILE_PREPARING_STATEMENT_EXCEPTION, e);
        throw new DaoException(ERROR_WHILE_PREPARING_STATEMENT_EXCEPTION, e);
    }

    private int handleCouldNotMapException(SQLException e) throws DaoException {
        logger.error(COULD_NOT_MAP_RESULT_EXCEPTION, e);
        throw new DaoException(COULD_NOT_MAP_RESULT_EXCEPTION, e);
    }

    private List<T> executeQuery(PreparedStatement statement) throws DaoException {
        List<T> results = new ArrayList<>();
        try (ResultSet resultset = statement.executeQuery()) {
            while (resultset != null && resultset.next()) {
                results.add(map(resultset));
            }
        } catch (SQLException e) {
            handleCouldNotMapException(e);
        }

        return results;
    }

    private void closeConnection(Connection connection) throws DaoException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error(COULD_NOT_CLOSE_CONNECTION_EXCEPTION, e);
            throw new DaoException(COULD_NOT_CLOSE_CONNECTION_EXCEPTION, e);
        }
    }

    protected Date toDate(LocalDate localDate) {
        return localDate != null ? Date.valueOf(localDate) : null;
    }

    protected LocalDate toLocalDate(Date date) throws SQLException {
        return date != null ? date.toLocalDate() : null;
    }
}
