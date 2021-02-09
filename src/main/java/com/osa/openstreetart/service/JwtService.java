package com.osa.openstreetart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections;

import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.dto.UserRegisterDto;

@Service
public class JwtService implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;

    @Autowired
	private PasswordEncoder bcryptEncoder;

     @Override
     public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		Optional<UserEntity> optionalUser = userRepo.findByEmail(username);
		if (optionalUser.isPresent()) {
			UserEntity user = optionalUser.get();
            //Question? user.getEmail() ou user.getUsername()

            //TODO: mettre les roles du user dans le userDetails pour les passer dans le jwt token
			return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with email: " + username);
		}
    }

    public UserEntity save(UserRegisterDto user) {

        //Note: à nettoyer après pour utiliser le modelmapper
        UserEntity newUser = new UserEntity();
        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getUsername());
        newUser.setRoles(Collections.singleton(user.getRole()));

        //hasher le mot de passe avec bcrypt
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));

        return userRepo.save(newUser);
    }
}
