package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.core.User;
import fr.ebiz.computerdatabase.persistence.dao.UserDao;
import fr.ebiz.computerdatabase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    /**
     * Constructor.
     *
     * @param userDao The user Dao
     */
    @Autowired
    UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getGrantedAuthorities(user)))
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    /**
     * Get the granted authorities for the user.
     *
     * @param user The user
     * @return The list of authorities for the users
     */
    private List<GrantedAuthority> getGrantedAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }
}
