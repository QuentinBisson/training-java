package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.AbstractDao;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ComputerDaoImpl extends AbstractDao<Computer, Integer> implements ComputerDao {

    private static final String COMPUTER_TABLE_NAME = "computer";
    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";
    private static final String INTRODUCED_COLUMN_NAME = "introduced";
    private static final String DISCONTINUED_COLUMN_NAME = "discontinued";
    private static final String COMPANY_ID_COLUMN_NAME = "company_id";

    public ComputerDaoImpl(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    protected String getTableName() {
        return COMPUTER_TABLE_NAME;
    }

    @Override
    protected String getIDSelector() {
        return ID_COLUMN_NAME + EQUAL_OPERATOR + "?";
    }

    @Override
    protected String getInsertQuery() {
        return INSERT_INTO_QUERY + getTableName()
                + "(name, introduced, discontinued, company_id) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateQuery() {
        return UPDATE_QUERY + getTableName() + " SET name = ?, introduced = ?, discontinued = ?, company_id = ?"
                + WHERE_OPERATOR + getIDSelector();
    }

    @Override
    public Computer map(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            Computer computer = new Computer();

            computer.setId(resultSet.getInt(ID_COLUMN_NAME));
            computer.setName(resultSet.getString(NAME_COLUMN_NAME));
            computer.setIntroduced(toLocalDate(resultSet.getDate(INTRODUCED_COLUMN_NAME)));
            computer.setDiscontinued(toLocalDate(resultSet.getDate(DISCONTINUED_COLUMN_NAME)));

            // Handle the possibility of having the company id as null
            Integer companyId = resultSet.getInt(COMPANY_ID_COLUMN_NAME);
            if (resultSet.wasNull()) {
                companyId = null;
            }
            computer.setCompanyId(companyId);
            return computer;
        }

        return null;
    }

    @Override
    protected void mapGeneratedId(ResultSet resultSet, Computer model) throws SQLException {
        if (resultSet != null && !resultSet.isClosed() && resultSet.next()) {
            model.setId(resultSet.getInt(FIRST_PARAMETER_INDEX));
        }
    }

    @Override
    protected void bindModel(PreparedStatement statement, Computer model, boolean updating) throws SQLException {
        int parameterIndex = FIRST_PARAMETER_INDEX;
        statement.setString(parameterIndex++, model.getName());

        if (model.getIntroduced() != null) {
            statement.setDate(parameterIndex++, toDate(model.getIntroduced()));
        } else {
            statement.setNull(parameterIndex++, Types.DATE);
        }
        if (model.getDiscontinued() != null) {
            statement.setDate(parameterIndex++, toDate(model.getDiscontinued()));
        } else {
            statement.setNull(parameterIndex++, Types.DATE);
        }
        if (model.getCompanyId() != null) {
            statement.setInt(parameterIndex++, model.getCompanyId());
        } else {
            statement.setNull(parameterIndex++, Types.INTEGER);
        }

        if (updating) {
            statement.setInt(parameterIndex++, model.getId());
        }
    }

    @Override
    protected void bindID(PreparedStatement statement, Integer id) throws SQLException {
        statement.setInt(FIRST_PARAMETER_INDEX, id);
    }

}
