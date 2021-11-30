package com.javainuse.service;

import com.javainuse.entity.UserEntity;
import com.javainuse.model.UserDTO;
import com.javainuse.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private UserRepo userDao;
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userDao.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        UserEntity newUser = user.get();
        
        return new User(newUser.getUsername(), newUser.getPassword(), new ArrayList<>());
    }

    public UserEntity save(UserDTO userDto) {
        UserEntity newUser = new UserEntity();
        newUser.setUsername(userDto.username());
        newUser.setPassword(passwordEncoder.encode(userDto.password()));
        return userDao.save(newUser);
    }
}