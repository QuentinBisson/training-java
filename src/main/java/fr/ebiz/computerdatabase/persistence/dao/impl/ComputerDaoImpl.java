package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.dao.DaoUtils;
import fr.ebiz.computerdatabase.persistence.dao.GetAllComputersRequest;
import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import fr.ebiz.computerdatabase.persistence.transaction.TransactionManager;
import fr.ebiz.computerdatabase.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class ComputerDaoImpl implements ComputerDao {

    private static final String READ_QUERY = "SELECT computer.id, computer.name as computerName, computer.introduced, computer.discontinued, computer.company_id, company.name as companyName FROM computer computer LEFT JOIN company company ON computer.company_id = company.id";
    private static final String READ_BY_ID_QUERY = "SELECT computer.id, computer.name as computerName, computer.introduced, computer.discontinued, computer.company_id, company.name as companyName from computer LEFT JOIN company company ON computer.company_id = company.id where computer.id = ?";
    private static final String COUNT_QUERY = "SELECT COUNT(*) from computer";
    private static final String INSERT_QUERY = "INSERT INTO computer(name, introduced, discontinued, company_id) VALUES (?, ?, ?, ?) ";
    private static final String UPDATE_QUERY = "UPDATE computer SET name = ?, introduced = ?, discontinued = ?, company_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM computer";
    private static final String DELETE_COMPUTERS_FOR_COMPANY_QUERY = "DELETE FROM computer WHERE company_id = ?";

    private static final int FIRST_PARAMETER_INDEX = 1;
    private static final String ID_COLUMN_NAME = "id";
    private static final String INTRODUCED_COLUMN_NAME = "introduced";
    private static final String DISCONTINUED_COLUMN_NAME = "discontinued";
    private static final String COMPANY_ID_COLUMN_NAME = "company_id";
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDaoImpl.class.getName());

    @Autowired
    private TransactionManager transactionManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Computer> get(int id) {
        try (PreparedStatement statement = transactionManager
                .getConnection()
                .prepareStatement(READ_BY_ID_QUERY)) {

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
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Computer> getAll(GetAllComputersRequest request) {
        String stringQuery = READ_QUERY;
        if (!StringUtils.isBlank(request.getQuery())) {
            stringQuery += " WHERE computer.name like ? OR company.name like ? ";
        }
        stringQuery += String.format(" ORDER BY %s %s LIMIT ? OFFSET ? ", request.getColumn().getField(), request.getOrder().name());
        try (PreparedStatement statement = transactionManager.getConnection().prepareStatement(stringQuery)) {

            int parameterIndex = FIRST_PARAMETER_INDEX;
            if (!StringUtils.isBlank(request.getQuery())) {
                String likeParameter = request.getQuery() + "%";
                statement.setString(parameterIndex++, likeParameter); // Computer name
                statement.setString(parameterIndex++, likeParameter); // Company name
            }

            statement.setInt(parameterIndex++, request.getPageSize());
            statement.setInt(parameterIndex, request.getOffset());

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Computer> found = new ArrayList<>();
                while (resultSet != null && resultSet.next()) {
                    found.add(mapEntity(resultSet));
                }
                return found;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count(String query) {
        String stringQuery = COUNT_QUERY;

        if (!StringUtils.isBlank(query)) {
            stringQuery += " LEFT JOIN company company ON computer.company_id = company.id WHERE computer.name like ? OR company.name like ? ";
        }
        try (PreparedStatement statement = transactionManager.getConnection().prepareStatement(stringQuery)) {

            int parameterIndex = FIRST_PARAMETER_INDEX;
            if (!StringUtils.isBlank(query)) {
                statement.setString(parameterIndex++, query + "%"); // Computer name
                statement.setString(parameterIndex, query + "%"); // Company name
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet != null && resultSet.next()) {
                    return resultSet.getInt(FIRST_PARAMETER_INDEX);
                }
            }

            return 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insert(final Computer computer) {
        try (PreparedStatement statement = transactionManager
                .getConnection()
                .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            bindParameters(computer, statement, FIRST_PARAMETER_INDEX);

            int affectedRows = statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                getGeneratedId(resultSet, computer);
            }

            return affectedRows == 1;

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            transactionManager.rollback();
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(final Computer computer) {
        try (PreparedStatement statement = transactionManager.getConnection().prepareStatement(UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            int parameterIndex = bindParameters(computer, statement, FIRST_PARAMETER_INDEX);
            statement.setInt(parameterIndex, computer.getId());

            int affectedRows = statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                getGeneratedId(resultSet, computer);
            }

            return affectedRows == 1;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            transactionManager.rollback();
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Integer id) {
        return deleteComputers(Collections.singletonList(id));
    }

    @Override
    public boolean deleteComputers(List<Integer> ids) {
        StringBuilder query = new StringBuilder(DELETE_QUERY + " WHERE ID IN (");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                query.append(",");
            }
            query.append("?");
        }
        query.append(")");
        try (PreparedStatement statement = transactionManager.getConnection().prepareStatement(query.toString())) {
            int index = FIRST_PARAMETER_INDEX;
            for (Integer id : ids) {
                statement.setInt(index++, id);
            }

            return statement.executeUpdate() == ids.size();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            transactionManager.rollback();
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
        }
    }

    /**
     * .
     * {@inheritDoc}
     */
    @Override
    public boolean deleteByCompanyId(Integer companyId) {
        try (PreparedStatement statement = transactionManager.getConnection().prepareStatement(DELETE_COMPUTERS_FOR_COMPANY_QUERY)) {
            statement.setInt(FIRST_PARAMETER_INDEX, companyId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            transactionManager.rollback();
            throw new DaoException(DaoUtils.DAO_ACCESS_ERROR, e);
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
        if (computer.getCompany() != null && computer.getCompany().getId() != null) {
            statement.setInt(parameterIndex++, computer.getCompany().getId());
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
                    .introduced(DaoUtils.toDate(resultSet.getTimestamp(INTRODUCED_COLUMN_NAME)))
                    .discontinued(DaoUtils.toDate(resultSet.getTimestamp(DISCONTINUED_COLUMN_NAME)));

            // Handle the possibility of having the company id as null
            Integer companyId = resultSet.getInt(COMPANY_ID_COLUMN_NAME);
            if (resultSet.wasNull()) {
                companyId = null;
            }
            builder.company(Company.builder()
                    .id(companyId)
                    .name(resultSet.getString("companyName"))
                    .build());
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
