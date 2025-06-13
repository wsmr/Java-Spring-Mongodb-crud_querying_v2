package com.diyawanna.sup.security;

import com.diyawanna.sup.service.AuthenticationService;
import com.diyawanna.sup.util.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * 
 * This filter intercepts HTTP requests and validates JWT tokens.
 * If a valid token is found, it sets the authentication in the security context.
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Get Authorization header
            String authHeader = request.getHeader(jwtUtil.getHeader());
            
            String username = null;
            String token = null;

            // Extract token from header
            if (authHeader != null && authHeader.startsWith(jwtUtil.getPrefix())) {
                token = jwtUtil.extractTokenFromHeader(authHeader);
                
                try {
                    username = jwtUtil.extractUsername(token);
                } catch (Exception e) {
                    logger.warn("Unable to extract username from JWT token: " + e.getMessage());
                }
            }

            // Validate token and set authentication
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = authenticationService.loadUserByUsername(username);
                    
                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        // Add user information to request attributes for easy access
                        request.setAttribute("currentUser", username);
                        request.setAttribute("jwtToken", token);
                    }
                } catch (Exception e) {
                    logger.warn("JWT token validation failed: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("JWT authentication filter error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Skip JWT validation for authentication endpoints
        return path.startsWith("/api/auth/") || 
               path.equals("/api/health") ||
               path.equals("/api/") ||
               path.startsWith("/api/public/");
    }
}

