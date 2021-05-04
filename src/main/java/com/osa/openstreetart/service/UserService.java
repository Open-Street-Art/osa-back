package com.osa.openstreetart.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.osa.openstreetart.dto.UserLoginDTO;
import com.osa.openstreetart.dto.UserPatchProfileDTO;
import com.osa.openstreetart.dto.UserProfileDTO;
import com.osa.openstreetart.dto.UserRegisterDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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

	@Autowired
	private PasswordEncoder bcryptEncoder;

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

		if (!dto.getPassword().equals(dto.getConfirmPassword()))
			throw new OSA400Exception("Passwords does not match.");

		if (!dto.getRole().equals(RoleEnum.ROLE_USER.name()) && !dto.getRole().equals(RoleEnum.ROLE_ARTIST.name()))
			throw new OSA400Exception("Invalid role.");

		jwtService.save(dto);
	}

	public String login(UserLoginDTO dto) {
		authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
		final UserDetails userDetails = jwtService.loadUserByUsername(dto.getUsername());
		return jwtUtil.generateToken(userDetails);
	}

	public UserProfileDTO loadUserProfileDTO(UserEntity user) {
		// Remplissage du DTO
		UserProfileDTO dto = new UserProfileDTO();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setDescription(user.getDescription());
		dto.setProfilePicture(user.getProfilePicture());
		dto.setIsPublic(user.getIsPublic());

		// Remplissage des roles dans la collection de String
		Collection<String> rolesString = new ArrayList<>();
		for (RoleEnum role : user.getRoles()) {
			rolesString.add(role.name());
		}
		dto.setRoles(rolesString);

		// Remplissage d'une collection des ID des oeuvres favorites
		Collection<ArtEntity> favArts = new ArrayList<>(user.getFavArts());
		dto.setFavArts(favArts);

		// Remplissage d'une collection des ID des villes favorites
		Collection<CityEntity> favCities = new ArrayList<>(user.getFavCities());
		dto.setFavCities(favCities);

		// Remplissage d'une collection des ID des artistes favoris
		Collection<UserEntity> favArtists = new ArrayList<>(user.getFavArtists());

		dto.setFavArtists(favArtists);
		return dto;
	}

	public void changeUserPassword(UserEntity user, String oldPassword, String newPassword) throws OSA400Exception {
		if (newPassword.length() < UserEntity.PSW_MIN_LENGTH)
			throw new OSA400Exception("Invalid new password.");

		if (!bcryptEncoder.matches(oldPassword, user.getPassword()))
			throw new OSA400Exception("Incorrect old password.");

		user.setPassword(bcryptEncoder.encode(newPassword));
		userRepo.save(user);
	}

	public void changerUserMail(UserEntity user, String newMail) throws OSA400Exception {
		if (!isValidEmailAddress(newMail))
			throw new OSA400Exception("Invalid new email address.");

		user.setEmail(newMail);
		userRepo.save(user);
	}

	public void patchUser(UserEntity user, UserPatchProfileDTO dto) throws OSA400Exception {
		if (dto.getIsPublic() == null)
			throw new OSA400Exception("isPublic is missing.");

		user.setDescription(dto.getDescription());
		user.setIsPublic(dto.getIsPublic());
		user.setProfilePicture(dto.getProfilePicture());
	}

	public boolean isValidEmailAddress(String email) {
		String ePattern = "^(.+)@(.+)$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
 	}
	
	public <T> UserEntity getOrFail(T value) throws OSA400Exception{

		Optional<UserEntity> optUser;
		if (value instanceof Integer)
			optUser = userRepo.findById((Integer)value);
		else
			optUser = userRepo.findByUsername((String)value);

		if (optUser.isEmpty())
			throw new OSA400Exception("User not found.");
		
		return optUser.get();
	}

	public void save(UserEntity user) {
		userRepo.save(user);
	}

	public Collection<UserEntity> findByUsernameWithSub(String substring) {
		return userRepo.findByUsernameWithSub(substring);
	}
	
}
