package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.DaoUtils;
import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComputerDaoImpl implements ComputerDao {

    private static final String READ_QUERY = "SELECT * from computer LIMIT ? OFFSET ?";
    private static final String READ_BY_ID_QUERY = "SELECT * from computer where  id = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(*) from computer where  id = ?";
    private static final String INSERT_QUERY = "INSERT INTO computer(name, introduced, discontinued, company_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE computer SET name = ?, introduced = ?, discontinued = ?, company_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM computer WHERE id = ?";

    private static final int FIRST_PARAMETER_INDEX = 1;
    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";
    private static final String INTRODUCED_COLUMN_NAME = "introduced";
    private static final String DISCONTINUED_COLUMN_NAME = "discontinued";
    private static final String COMPANY_ID_COLUMN_NAME = "company_id";
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDaoImpl.class.getName());
    private static final String TOO_MANY_RESULTS_WERE_FOUND_FOR_ID_EXCEPTION = "Too many results were found for id = ";

    private static ComputerDao instance;
    private DaoFactory daoFactory;

    private ComputerDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public synchronized static ComputerDao getInstance() {
        if (instance == null) {
            instance = new ComputerDaoImpl(DaoFactory.getInstance());
        }
        return instance;
    }

    @Override
    public Optional<Computer> get(int id) {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(READ_BY_ID_QUERY)) {

            statement.setInt(FIRST_PARAMETER_INDEX, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Computer> found = new ArrayList<>();
                while (resultSet != null && resultSet.next()) {
                    found.add(mapEntity(resultSet));
                }

                if (found.isEmpty()) {
                    return Optional.empty();
                } else if (found.size() > 1) {
                    String message = TOO_MANY_RESULTS_WERE_FOUND_FOR_ID_EXCEPTION + id;
                    LOGGER.error(message);
                    throw new DaoException(message);
                }
                return Optional.of(found.get(0));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    @Override
    public List<Computer> getAll(int elements, int offset) throws DaoException {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(READ_QUERY)) {

            int parameterIndex = FIRST_PARAMETER_INDEX;
            statement.setInt(parameterIndex++, elements);
            statement.setInt(parameterIndex, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Computer> found = new ArrayList<>();
                while (resultSet != null && resultSet.next()) {
                    found.add(mapEntity(resultSet));
                }
                return found;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    @Override
    public int count() throws DaoException {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet != null && resultSet.next()) {
                return resultSet.getInt(FIRST_PARAMETER_INDEX);
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    @Override
    public boolean insert(final Computer computer) {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            bindParameters(computer, statement, FIRST_PARAMETER_INDEX);

            int affectedRows = statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                getGeneratedId(resultSet, computer);
            }

            return affectedRows == 1;

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    @Override
    public boolean update(final Computer computer) {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            int parameterIndex = FIRST_PARAMETER_INDEX;
            bindParameters(computer, statement, parameterIndex);
            statement.setInt(parameterIndex, computer.getId());

            int affectedRows = statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                getGeneratedId(resultSet, computer);
            }

            return affectedRows == 1;

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DaoException {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(FIRST_PARAMETER_INDEX, id);

            int affectedRows = statement.executeUpdate();

            return affectedRows == 1;

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    private int bindParameters(Computer computer, PreparedStatement statement, int parameterIndex) throws SQLException {
        statement.setString(parameterIndex++, computer.getName());

        if (computer.getIntroduced() != null) {
            statement.setTimestamp(parameterIndex++, DaoUtils.toTimestamp(computer.getIntroduced()));
        } else {
            statement.setNull(parameterIndex++, Types.DATE);
        }
        if (computer.getDiscontinued() != null) {
            statement.setTimestamp(parameterIndex++, DaoUtils.toTimestamp(computer.getDiscontinued()));
        } else {
            statement.setNull(parameterIndex++, Types.DATE);
        }
        if (computer.getCompanyId() != null) {
            statement.setInt(parameterIndex++, computer.getCompanyId());
        } else {
            statement.setNull(parameterIndex++, Types.INTEGER);
        }
        return parameterIndex;
    }

    private Computer mapEntity(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            Computer.ComputerBuilder builder = Computer.builder()
                    .id(resultSet.getInt(ID_COLUMN_NAME))
                    .name(resultSet.getString(NAME_COLUMN_NAME))
                    .introduced(DaoUtils.toDate(resultSet.getTimestamp(INTRODUCED_COLUMN_NAME)))
                    .discontinued(DaoUtils.toDate(resultSet.getTimestamp(DISCONTINUED_COLUMN_NAME)));

            // Handle the possibility of having the company id as null
            Integer companyId = resultSet.getInt(COMPANY_ID_COLUMN_NAME);
            if (resultSet.wasNull()) {
                companyId = null;
            }
            builder.companyId(companyId);
            return builder.build();
        }

        return null;
    }

    private void getGeneratedId(ResultSet resultSet, Computer model) throws SQLException {
        if (resultSet != null && !resultSet.isClosed() && resultSet.next()) {
            model.setId(resultSet.getInt(FIRST_PARAMETER_INDEX));
        }
    }

}
