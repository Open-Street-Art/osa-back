package com.osa.openstreetart.service;

import java.util.Optional;

import com.osa.openstreetart.dto.UserLoginDTO;
import com.osa.openstreetart.dto.UserRegisterDTO;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA409Exception;
import com.osa.openstreetart.repository.UserRepository;
import com.osa.openstreetart.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private JwtUtil jwtUtil;

	public void register(UserRegisterDTO dto) throws OSA409Exception, OSA400Exception {
		Optional<UserEntity> optUser1 = userRepo.findByEmail(dto.getEmail());
		Optional<UserEntity> optUser2 = userRepo.findByUsername(dto.getUsername());
		if (optUser1.isPresent() || optUser2.isPresent())
			throw new OSA409Exception("User already existing.");
	
		if (!isValidEmailAddress(dto.getEmail()))
			throw new OSA400Exception("Invalid email adress.");

		if (dto.getUsername().length() < UserEntity.USERNAME_MIN_LENGTH)
			throw new OSA400Exception("Username too short.");

		if (dto.getPassword().length() < UserEntity.PSW_MIN_LENGTH)
			throw new OSA400Exception("Password too short.");

		if (!dto.getRole().equals(RoleEnum.ROLE_USER.name()) && !dto.getRole().equals(RoleEnum.ROLE_ARTIST.name()))
			throw new OSA400Exception("Invalid role.");

		jwtService.save(dto);
	}

	public String login(UserLoginDTO dto) {
		authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
		final UserDetails userDetails = jwtService.loadUserByUsername(dto.getUsername());
		return jwtUtil.generateToken(userDetails);
	}

	public boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
 	}

}
