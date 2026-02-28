package com.joaopenascimento.backend.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.joaopenascimento.backend.repositories.UserRepository;
import com.joaopenascimento.backend.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        var token = this.recoverToken(request);
        
        if(token != null){
            var login = tokenService.validateToken(token);
            System.out.println("Token encontrado. Login extraído: " + login);

            if(login != null && !login.isEmpty()) {
                var userEntity = userRepository.findByEmail(login).orElseThrow(() -> new RuntimeException("User Not Found"));
                
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .builder()
                        .username(userEntity.getEmail())
                        .password(userEntity.getPassword())
                        .roles(userEntity.getRole().name())
                        .build();

                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Usuário autenticado no contexto: " + login);
            } else {
                System.out.println("Token inválido ou expirado.");
            }
        } else {
            System.out.println("Header Authorization não encontrado ou vazio.");
        }
        
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "").trim();
    }
}