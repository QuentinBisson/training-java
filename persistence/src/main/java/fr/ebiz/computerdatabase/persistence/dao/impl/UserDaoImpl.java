package fr.ebiz.computerdatabase.persistence.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import fr.ebiz.computerdatabase.core.QUser;
import fr.ebiz.computerdatabase.core.User;
import fr.ebiz.computerdatabase.persistence.dao.UserDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(
                new JPAQuery(entityManager)
                        .from(QUser.user)
                        .where(QUser.user.username.eq(username))
                        .uniqueResult(QUser.user));
    }
}
