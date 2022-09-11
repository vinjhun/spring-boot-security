/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.myapp.security;

import com.security.myapp.entity.User;
import com.security.myapp.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author user
 */
@Service
@Transactional
public class AuthUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    
    private static final Logger logger = LoggerFactory.getLogger(AuthUserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("UserDetailsService Authentication");
        
        //return new UserDetails
        Optional<User> result = userRepo.findByUserName(username);
        User obj;

        if (!result.isPresent()) {
            throw new UsernameNotFoundException("Unable to find Username of  " + username);
        } else {
            obj = result.get();
        }
        
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(obj.getUserName(), obj.getPassword(), authorities);
    }
}
