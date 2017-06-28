package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.persistence.dao.AbstractDao;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyDaoImpl extends AbstractDao<Company, Integer> implements CompanyDao {

    private static final String COMPANY_TABLE_NAME = "company";
    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";

    public CompanyDaoImpl(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    protected String getTableName() {
        return COMPANY_TABLE_NAME;
    }

    @Override
    protected String getIDSelector() {
        return ID_COLUMN_NAME + EQUAL_OPERATOR + "?";
    }

    @Override
    protected String getInsertQuery() {
        return INSERT_INTO_QUERY + getTableName() + "(name) VALUES (?" + NAME_COLUMN_NAME + ")";
    }

    @Override
    protected String getUpdateQuery() {
        return UPDATE_QUERY + getTableName() + " SET name = ?" + WHERE_OPERATOR + getIDSelector();
    }

    @Override
    public Company map(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            Company company = new Company();

            company.setId(resultSet.getInt(ID_COLUMN_NAME));
            company.setName(resultSet.getString(NAME_COLUMN_NAME));
            return company;
        }

        return null;
    }

    @Override
    protected void mapGeneratedId(ResultSet resultSet, Company model) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            model.setId(resultSet.getInt(FIRST_PARAMETER_INDEX));
        }
    }

    @Override
    public void bindModel(PreparedStatement statement, Company model, boolean updating) throws SQLException {
        int parameterIndex = FIRST_PARAMETER_INDEX;
        statement.setString(parameterIndex++, model.getName());

        if (updating) {
            statement.setInt(parameterIndex++, model.getId());
        }
    }

    @Override
    public void bindID(PreparedStatement statement, Integer id) throws SQLException {
        statement.setInt(FIRST_PARAMETER_INDEX, id);
    }

}