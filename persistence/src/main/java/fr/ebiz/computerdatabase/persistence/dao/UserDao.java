package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.core.User;

import java.util.Optional;

public interface UserDao {

    /**
     * Find a user from the database.
     *
     * @param username The name of the user
     * @return The company if it exists or Optional.empty() if it does not
     */
    Optional<User> findByUsername(String username);

}
