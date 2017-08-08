package fr.ebiz.computerdatabase.persistence.dao.impl;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.ComparableExpressionBase;
import fr.ebiz.computerdatabase.core.Computer;
import fr.ebiz.computerdatabase.core.QCompany;
import fr.ebiz.computerdatabase.core.QComputer;
import fr.ebiz.computerdatabase.persistence.SortOrder;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class ComputerDaoImpl implements ComputerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Computer> get(int id) {
        return Optional.ofNullable(new JPAQuery(entityManager).from(QComputer.computer).where(QComputer.computer.id.eq(id)).uniqueResult(QComputer.computer));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Computer> getAll(String search, int pageSize, int offset, SortColumn column, SortOrder order) {
        JPQLQuery query = new JPAQuery(entityManager).from(QComputer.computer).innerJoin(QComputer.computer.company, QCompany.company);
        if (!StringUtils.isEmpty(search)) {
            query = query
                    .where(
                            QComputer.computer.name.like("%" + query + "%"),
                            QComputer.computer.company.name.like("%" + query + "%"));
        }

        ComparableExpressionBase<?> orderColumn = column.getField();
        return query
                .limit(pageSize).offset(offset)
                .orderBy(order == SortOrder.ASC ? orderColumn.asc() : orderColumn.desc())
                .list(QComputer.computer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count(String query) {
        return (int) new JPAQuery(entityManager).from(QComputer.computer).count();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(final Computer computer) {
        entityManager.persist(computer);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final Computer computer) {
        entityManager.merge(computer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Integer id) {
        deleteComputers(Collections.singletonList(id));
    }

    @Override
    public void deleteComputers(List<Integer> ids) {
        new JPADeleteClause(entityManager, QComputer.computer)
                .where(QComputer.computer.id.in(ids))
                .execute();
    }

    /**
     * .
     * {@inheritDoc}
     */
    @Override
    public void deleteByCompanyId(int companyId) {
        new JPADeleteClause(entityManager, QComputer.computer)
                .where(QCompany.company.id.eq(companyId))
                .execute();
    }

}
