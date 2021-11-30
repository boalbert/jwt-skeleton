package com.javainuse.service;

import java.util.ArrayList;

import com.javainuse.dao.UserDao;
import com.javainuse.model.DAOUser;
import com.javainuse.model.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

	private UserDao userDao;
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		DAOUser user = userDao.findByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
	}

	public DAOUser save(UserDTO userDto) {
		DAOUser newUser = new DAOUser();
		newUser.setUsername(userDto.getUsername());
		newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
		return userDao.save(newUser);
	}
}