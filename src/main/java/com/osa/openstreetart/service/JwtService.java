package com.osa.openstreetart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import java.util.List;

import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.tranformator.UserRegisterTransformator;
import com.osa.openstreetart.util.JwtUtil;
import com.osa.openstreetart.dto.UserRegisterDTO;

@Service
public class JwtService implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Autowired
	private UserRegisterTransformator userTransf;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		Optional<UserEntity> optUser = userRepo.findByUsername(username);
		if (!optUser.isPresent())
			throw new UsernameNotFoundException("User not found with email: " + username);

		List<GrantedAuthority> roles = new ArrayList<>();
		for (RoleEnum role : optUser.get().getRoles())
			roles.add(new SimpleGrantedAuthority(role.name()));

		return new User(optUser.get().getUsername(), optUser.get().getPassword(), roles);
	}

	public UserEntity save(UserRegisterDTO user) {
		UserEntity newUser = userTransf.dtoToModel(user);
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userRepo.save(newUser);
	}

	public Collection<RoleEnum> getRolesByToken(String token) {
		String payload = jwtUtil.getRolesFromToken(token);
		Collection<RoleEnum> roles = new ArrayList<>();
		if (!payload.isEmpty())
			for (String role : payload.split(","))
				roles.add(RoleEnum.valueOf(role));

		return roles;
	}

}
