package com.joaopenascimento.backend.controllers;

import com.joaopenascimento.backend.dto.auth.LoginDTO;
import com.joaopenascimento.backend.dto.auth.RegisterDTO;
import com.joaopenascimento.backend.dto.auth.TokenDTO;
import com.joaopenascimento.backend.model.User;
import com.joaopenascimento.backend.services.TokenService;
import com.joaopenascimento.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var userDetails = (UserDetails) auth.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());
        var token = tokenService.generateToken(user);

        return ResponseEntity.ok(new TokenDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        userService.register(data);
        return ResponseEntity.ok().build();
    }
}
