package com.restapi.test.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request, 
                                    @SuppressWarnings("null") HttpServletResponse response, 
                                    @SuppressWarnings("null") FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String jwt = null;
        String username = null;

        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
              if (isProtectedEndpoint(request)) throw new RuntimeException("Authorization header is missing or does not contain Bearer token");

              filterChain.doFilter(request, response);
              return;
            }

            jwt = authorizationHeader.substring(7);

            username = jwtUtil.extractUsername(jwt);

            if (jwt == null || username == null) throw new RuntimeException("Token is missing or invalid");

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (!jwtUtil.validateToken(jwt, username)) throw new RuntimeException("Token is invalid or expired");
            
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, null);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
          }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            
            response.getWriter().write(new ObjectMapper().writeValueAsString(
              Collections.singletonMap("error", e.getMessage())
            ));

            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isProtectedEndpoint(HttpServletRequest request) {
      String path = request.getRequestURI();

      return
        !path.equals("/api/register") && 
        !path.equals("/api/login") &&

        !path.equals("/api/google") &&
        
        !path.equals("/login/success");
  }
}

