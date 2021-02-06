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
     @Autowired
     private UserRepository userRepository;

     @Autowired
	private PasswordEncoder bcryptEncoder;

     @Override
     public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
          UserEntity user = null;
          try {
               user = userRepository.findByEmail(email);
               return new org.springframework.security.core.userdetails
               .User(user.getEmail(), user.getPassword(), new ArrayList<>());
          } catch (Exception e) {
               throw new UsernameNotFoundException("User with email: " + email + "not found");
          }
     }

     public UserEntity save(UserRegisterDto user) {
          UserEntity newUser = new UserEntity();
          newUser.setEmail(user.getEmail());
          newUser.setUsername(user.getEmail());

          //hasher le mot de passe avec bcrypt
          newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
          return userRepository.save(newUser);
     }
}
