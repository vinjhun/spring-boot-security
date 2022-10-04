/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.myapp.controller;

import com.security.myapp.model.UserModel;
import com.security.myapp.repository.UserRepository;
import com.security.myapp.security.AuthUserService;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Suppose to write Mapper Though
 */
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthUserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("version")
    public ResponseEntity<String> getVersion() {
        return new ResponseEntity<>("1.0", HttpStatus.OK);
    }
    
//    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> login(HttpServletRequest req, @RequestBody(required = false) UserModel model) throws Throwable {
//        logger.info("=====> Auth Login");
//        if (!StringUtils.hasText(model.getUserName())) {
//            throw new BadCredentialsException("");
//        }
//
//        //User result = userRepo.findByUserName(model.getUserName()).orElse(null);
//        UserDetails user = userService.loadUserByUsername(model.getUserName());
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        return new ResponseEntity<>("OK", HttpStatus.OK);
//    }

}
