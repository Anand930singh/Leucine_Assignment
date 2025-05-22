package com.example.todo.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    private final JWTUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JWTFilter(JWTUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        logger.info("Processing request: " + requestURI);
        
        // Allow access to /api/auth/** endpoints without authentication
        if (requestURI.startsWith("/api/auth/")) {
            logger.info("Allowing access to auth endpoint: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");
        logger.info("Auth header: " + (authHeader != null ? authHeader : "missing"));
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("Invalid or missing Authorization header");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            logger.info("Extracted JWT token: " + jwt);
            
            final String username = jwtUtils.getUsernameFromToken(jwt)
                .map(user -> user.getUsername())
                .orElse(null);
            
            logger.info("Username from token: " + (username != null ? username : "null"));

            if (username != null) {
                logger.info("Loading user details for username: " + username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.info("User details loaded: " + (userDetails != null ? "success" : "failed"));
                
                if (jwtUtils.isTokenValid(jwt, username)) {
                    logger.info("Token is valid, setting authentication");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Authentication set successfully");
                } else {
                    logger.error("Token validation failed for username: " + username);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } else {
                logger.error("Username is null from token");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error processing JWT token: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
} 