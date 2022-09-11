/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.myapp.security.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author user
 */
public class CustomAuthenticationHandler extends UsernamePasswordAuthenticationFilter {

    private static Logger logger = LoggerFactory.getLogger(CustomAuthenticationHandler.class);
    private static ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    protected AuthenticationManager getAuthenticationManager() {
        return super.getAuthenticationManager();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            String requestBody = IOUtils.toString(request.getReader());
            ObjectNode node = mapper.convertValue(requestBody, ObjectNode.class);

            if (node == null) {
                throw new BadCredentialsException("Unrecognized format of request body");
            }
            
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    node.get("id").asText(), node.get("password").asText());
            
            // Allow subclasses to set the "details" property
            setDetails(request, authentication);

            return this.getAuthenticationManager().authenticate(authentication);
        } catch (IOException ex) {
            logger.error("", ex);
            throw new InternalAuthenticationServiceException("", ex);
        }
    }

}
