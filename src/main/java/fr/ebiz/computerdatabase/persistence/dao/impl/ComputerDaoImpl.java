package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.DaoUtils;
import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComputerDaoImpl implements ComputerDao {

    private static final String READ_QUERY = "SELECT computer.id, computer.name as computerName, computer.introduced, computer.discontinued, computer.company_id, company.name as companyName from computer computer LEFT JOIN company company ON computer.company_id = company.id where lower(computer.name) like ? order by computer.name LIMIT ? OFFSET ?";
    private static final String READ_BY_ID_QUERY = "SELECT computer.id, computer.name as computerName, computer.introduced, computer.discontinued, computer.company_id, company.name as companyName from computer LEFT JOIN company company ON computer.company_id = company.id where computer.id = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(*) from computer where lower(name) like ?";
    private static final String INSERT_QUERY = "INSERT INTO computer(name, introduced, discontinued, company_id) VALUES (?, ?, ?, ?) ";
    private static final String UPDATE_QUERY = "UPDATE computer SET name = ?, introduced = ?, discontinued = ?, company_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM computer WHERE id = ?";

    private static final int FIRST_PARAMETER_INDEX = 1;
    private static final String ID_COLUMN_NAME = "id";
    private static final String INTRODUCED_COLUMN_NAME = "introduced";
    private static final String DISCONTINUED_COLUMN_NAME = "discontinued";
    private static final String COMPANY_ID_COLUMN_NAME = "company_id";
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDaoImpl.class.getName());

    private static ComputerDao instance;
    private final DaoFactory daoFactory;

    /**
     * Service constructor used to inject a {@link DaoFactory} instance.
     *
     * @param daoFactory The dao factory to inject
     */
    private ComputerDaoImpl(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Get the dao instance.
     * Creates it thread-safe if it does not exist.
     *
     * @return the dao singleton instance
     */
    public static ComputerDao getInstance() {
        if (instance == null) {
            synchronized (ComputerDaoImpl.class) {
                if (instance == null) {
                    instance = new ComputerDaoImpl(DaoFactory.getInstance());
                }
            }
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
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

                return DaoUtils.checkOnlyOne(found, LOGGER);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Computer> getAll(String nameQuery, int elements, int offset) {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(READ_QUERY)) {

            int parameterIndex = FIRST_PARAMETER_INDEX;
            statement.setString(parameterIndex++, "%" + nameQuery + "%");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int count(String nameQuery) {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_QUERY)) {

            statement.setString(FIRST_PARAMETER_INDEX, "%" + nameQuery + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet != null && resultSet.next()) {
                    return resultSet.getInt(FIRST_PARAMETER_INDEX);
                }
            }

            return 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException("Dao access error", e);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(final Computer computer) {
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            int parameterIndex = bindParameters(computer, statement, FIRST_PARAMETER_INDEX);
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

    /**
     * .
     * {@inheritDoc}
     */
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

    /**
     * Bind query parameters from the model in the prepared statement.
     *
     * @param computer       The computer to bind properties from
     * @param statement      The statement to bind
     * @param parameterIndex The parameter index
     * @return the current parameter index in case we need to add other parameters
     * @throws SQLException if an error occurs when binding values in the statement
     */
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

    /**
     * Map a {@link ResultSet} to a {@link Computer} entity.
     *
     * @param resultSet The result set to extract data from
     * @return The mapped entity
     * @throws SQLException if an error occurs when accessing the properties from the result set
     */
    private Computer mapEntity(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            Computer.ComputerBuilder builder = Computer.builder()
                    .id(resultSet.getInt(ID_COLUMN_NAME))
                    .name(resultSet.getString("computerName"))
                    .companyName(resultSet.getString("companyName"))
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

    /**
     * Get the generated if from the result set and update the domain object.
     *
     * @param resultSet The result set to extract generated ids from
     * @param model     The model to update
     * @throws SQLException if an error occurs when accessing the properties from the result set
     */
    private void getGeneratedId(ResultSet resultSet, Computer model) throws SQLException {
        if (resultSet != null && !resultSet.isClosed() && resultSet.next()) {
            model.setId(resultSet.getInt(FIRST_PARAMETER_INDEX));
        }
    }

}
