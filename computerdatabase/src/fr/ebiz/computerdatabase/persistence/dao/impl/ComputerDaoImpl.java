package fr.ebiz.computerdatabase.persistence.dao.impl;

import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.AbstractDao;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.persistence.factory.DaoFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComputerDaoImpl extends AbstractDao<Computer, Integer> implements ComputerDao {

    private static final String COMPUTER_TABLE_NAME = "computer";

    public ComputerDaoImpl(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    protected String getTableName() {
        return COMPUTER_TABLE_NAME;
    }

    @Override
    protected String getIDSelector() {
        return Columns.id.name() + EQUAL_OPERATOR + ":id";
    }

    @Override
    protected String getInsertQuery() {
        return INSERT_INTO_QUERY + getTableName()
                + "(name, introduced, discontinued, company_id) VALUES (:" + Columns.name.name()
                + ", :" + Columns.introduced.name()
                + ", :" + Columns.discontinued.name()
                + ", :" + Columns.company.name() + ")";
    }

    @Override
    protected String getUpdateQuery() {
        return UPDATE_QUERY + getTableName() + " SET name = :" + Columns.name.name()
                + ", introduced = :" + Columns.introduced.name()
                + ", discontinued = :" + Columns.discontinued.name()
                + ", company_id = :" + Columns.company.name()
                + WHERE_OPERATOR + getIDSelector();
    }

    @Override
    public Computer map(ResultSet resultSet) throws SQLException {
        if (resultSet != null && !resultSet.isClosed()) {
            Computer computer = new Computer();

            computer.setId(resultSet.getInt(Columns.id.name()));
            computer.setName(resultSet.getString(Columns.name.name()));
            computer.setIntroduced(toLocalDate(resultSet.getDate(Columns.introduced.name())));
            computer.setDiscontinued(toLocalDate(resultSet.getDate(Columns.discontinued.name())));
            return computer;
        }

        return null;
    }

    @Override
    protected void bindModel(PreparedStatement statement, Computer model) throws SQLException {
        statement.setString(Columns.name.getIndex(), model.getName());
        statement.setDate(Columns.introduced.getIndex(), toDate(model.getIntroduced()));
        statement.setDate(Columns.discontinued.getIndex(), toDate(model.getDiscontinued()));
        statement.setInt(Columns.company.getIndex(), model.getCompanyId());
        bindID(statement, model.getId());
    }

    @Override
    protected void bindID(PreparedStatement statement, Integer id) throws SQLException {
        statement.setInt(Columns.id.getIndex(), id);
    }

    private enum Columns {
        id, name, introduced, discontinued, company;

        public int getIndex() {
            return ordinal() + 1;
        }
    }
}
