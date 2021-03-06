package com.javainuse.controller;

import com.javainuse.config.jwt.JwtTokenUtil;
import com.javainuse.entity.UserEntity;
import com.javainuse.model.UserDTO;
import com.javainuse.model.UserRequest;
import com.javainuse.model.UserResponse;
import com.javainuse.service.JwtUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@AllArgsConstructor
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService jwtInMemoryUserDetailsService;
    private JwtUserDetailsService userDetailsService;

    @PostMapping("/authenticate")
    public ResponseEntity<UserResponse> createAuthenticationToken(@RequestBody UserRequest authenticationRequest) {
        authenticate(authenticationRequest.username(), authenticationRequest.password());

        final var userDetails = jwtInMemoryUserDetailsService
                .loadUserByUsername(authenticationRequest.username());

        final var token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new UserResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> saveUser(@RequestBody UserDTO user) {
        return new ResponseEntity<>(userDetailsService.save(user), HttpStatus.CREATED);
    }

    private void authenticate(String username, String password) throws DisabledException, BadCredentialsException {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }
}
