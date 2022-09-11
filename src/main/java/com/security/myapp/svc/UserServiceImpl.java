/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.myapp.svc;

import com.security.myapp.repository.UserRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Lazy to implement Mapper
 */
@Service
@Transactional
public class UserServiceImpl {
    
    @Autowired
    private UserRepository userRepository;
    
    
    
}
