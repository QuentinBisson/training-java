package fr.ebiz.computerdatabase.persistence.dao.impl;

import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;
import fr.ebiz.computerdatabase.core.Company;
import fr.ebiz.computerdatabase.core.QCompany;
import fr.ebiz.computerdatabase.persistence.dao.CompanyDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class CompanyDaoImpl implements CompanyDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Company> get(int id) {
        return Optional.ofNullable(
                new JPAQuery(entityManager)
                        .from(QCompany.company)
                        .where(QCompany.company.id.eq(id))
                        .uniqueResult(QCompany.company));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Company> getAll(int pageSize, int offset) {
        return new JPAQuery(entityManager).from(QCompany.company)
                .limit(pageSize)
                .offset(offset)
                .list(QCompany.company);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        return (int) new JPAQuery(entityManager).from(QCompany.company)
                .count();
    }

    /**
     * .
     * {@inheritDoc}
     */
    @Override
    public void delete(Integer id) {
        new JPADeleteClause(entityManager, QCompany.company)
                .where(QCompany.company.id.eq(id))
                .execute();
    }


}