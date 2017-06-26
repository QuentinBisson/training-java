package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.persistence.Dao;
import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T, ID> implements Dao<T, ID> {

    protected static final String INSERT_INTO_QUERY = "INSERT INTO ";
    protected static final String UPDATE_QUERY = "UPDATE ";
    protected static final String WHERE_OPERATOR = " WHERE ";
    protected static final String EQUAL_OPERATOR = " = ";
    private static final String SELECT_QUERY = "SELECT * FROM ";
    private static final String DELETE_QUERY = "DELETE FROM ";
    private static final String COUNTER_ALIAS = "CNT";
    private static final String COUNT_QUERY = "SELECT COUNT(*) as " + COUNTER_ALIAS + " FROM ";
    private DaoFactory daoFactory;

    protected AbstractDao(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    protected abstract String getTableName();

    protected abstract String getIDSelector();

    protected abstract String getInsertQuery();

    protected abstract String getUpdateQuery();

    protected abstract void bindID(PreparedStatement statement, ID id) throws SQLException;

    protected abstract void bindModel(PreparedStatement statement, T model) throws SQLException;

    protected abstract T map(ResultSet resultset) throws SQLException;

    @Override
    public Optional<T> get(ID id) {
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY + getTableName() + WHERE_OPERATOR + getIDSelector())) {
                bindID(statement, id);
                List<T> found = executeQuery(statement);
                if (found.isEmpty()) {
                    return Optional.empty();
                } else if (found.size() > 1) {
                    // TODO Handle this
                    throw new DaoException("Too many results were found for id = " + id);
                }
                return Optional.of(found.get(0));
            } catch (SQLException e) {
                // TODO Handle this
                throw new DaoException("Error while preparing statement", e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public List<T> getAll() {
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY + getTableName())) {
                return executeQuery(statement);
            } catch (SQLException e) {
                // TODO Handle this
                throw new DaoException("Error while preparing statement", e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public int count() {
        String queryString = COUNT_QUERY + getTableName();
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(queryString)) {
                try (ResultSet resultset = statement.executeQuery()) {

                    if (resultset != null && resultset.next()) {
                        return resultset.getInt(COUNTER_ALIAS);
                    }

                    return 0;

                } catch (SQLException e) {
                    // TODO Handle this
                    throw new DaoException("Could not map result", e);
                }
            } catch (SQLException e) {
                // TODO Handle this
                throw new DaoException("Error while preparing statement", e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public void insert(T model) {
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(getInsertQuery())) {
                bindModel(statement, model);
                statement.executeUpdate();
            } catch (SQLException e) {
                // TODO Handle this
                throw new DaoException("Error while preparing statement", e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public void update(T model) {
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(getUpdateQuery())) {
                bindModel(statement, model);
                statement.executeUpdate();
            } catch (SQLException e) {
                // TODO Handle this
                throw new DaoException("Error while preparing statement", e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public void delete(ID id) {
        Connection connection = null;
        try {
            connection = daoFactory.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY + getTableName() + WHERE_OPERATOR + getIDSelector())) {
                bindID(statement, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                // TODO Handle this
                throw new DaoException("Error while preparing statement", e);
            }
        } finally {
            closeConnection(connection);
        }
    }

    private List<T> executeQuery(PreparedStatement statement) {
        List<T> results = new ArrayList<>();
        try (ResultSet resultset = statement.executeQuery()) {
            while (resultset != null && resultset.next()) {
                results.add(map(resultset));
            }
            return results;
        } catch (SQLException e) {
            // TODO Handle this
            throw new DaoException("Could not map result", e);
        }
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // TODO Handle this
            throw new DaoException("Could not close connection", e);
        }
    }

    protected Date toDate(LocalDate localDate) {
        return localDate != null ? Date.valueOf(localDate) : null;
    }

    protected LocalDate toLocalDate(Date date) throws SQLException {
        return date != null ? date.toLocalDate() : null;
    }
}
