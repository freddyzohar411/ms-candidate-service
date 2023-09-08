package com.avensys.rts.candidate.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Kotaiah nalleboina
 * This class is used to handle JWT Auth token validation.
 */
public class AuthInterceptor implements HandlerInterceptor {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOGGER.info("Auth Pre-handling");

        //Implement JWT Auth token validation here and check with keycloak server if token is valid



        return true; // Continue the request processing chain
    }

}


