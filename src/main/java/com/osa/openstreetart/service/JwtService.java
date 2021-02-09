package com.osa.openstreetart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.dto.UserRegisterDto;

@Service
public class JwtService implements UserDetailsService{
     static public final int PSW_MIN_LENGTH = 8;
     
     @Autowired
     private UserRepository userRepository;

     @Autowired
	private PasswordEncoder bcryptEncoder;

     @Override
     public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
          UserEntity user = null;
          try {
               user = userRepository.findByUsername(username);
               return new org.springframework.security.core.userdetails
               .User(user.getUsername(), user.getPassword(), new ArrayList<>());
          } catch (Exception e) {
               throw new UsernameNotFoundException("Username: " + username + "not found");
          }
     }

     public UserEntity save(UserRegisterDto user) {
          UserEntity newUser = new UserEntity();
          newUser.setEmail(user.getEmail());
          newUser.setUsername(user.getUsername());

          //hasher le mot de passe avec bcrypt
          newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
          return userRepository.save(newUser);
     }
}
