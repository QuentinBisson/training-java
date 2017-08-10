package fr.ebiz.computerdatabase.core;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "user")
public class User {
    /**
     * Company uuid.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * Username.
     */
    private String username;

    /**
     * The password.
     */
    private String password;

    /**
     * .
     * Create a User instance
     *
     * @return a new user builder
     */
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private static class UserBuilder {
        private final User user;

        /**
         * Create a new user object to build.
         */
        UserBuilder() {
            user = new User();
        }

        /**
         * Set the user id.
         *
         * @param id The new id to set
         * @return The builder instance
         */
        public UserBuilder id(Integer id) {
            user.id = id;
            return this;
        }

        /**
         * Set the username.
         *
         * @param username The new username to set
         * @return The builder instance
         */
        public UserBuilder username(String username) {
            user.username = username;
            return this;
        }

        /**
         * Set the password.
         *
         * @param password The new password date to set
         * @return The builder instance
         */
        public UserBuilder password(String password) {
            user.password = password;
            return this;
        }

        /**
         * .
         * Return the built instance of User
         *
         * @return the user
         */
        public User build() {
            return user;
        }
    }
}
