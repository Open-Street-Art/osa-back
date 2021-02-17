package com.osa.openstreetart.tranformator;

import javax.transaction.Transactional;

import com.osa.openstreetart.dto.UserRegisterDTO;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class UserRegisterTransformator implements AbstractTransformator<UserEntity, UserRegisterDTO> {
	
	private static ModelMapper modelMapper;

	private static ModelMapper modelMapper() {
		if (modelMapper == null) {
			modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
			modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(PRIVATE);
		}
		return modelMapper;
	}

	@Override
	public UserRegisterDTO modelToDto(final UserEntity model) {
		if (null == model) {
			return null;
		}
		return modelMapper().map(model, UserRegisterDTO.class);
	}

	@Override
	public UserEntity dtoToModel(final UserRegisterDTO dto) {
		if (null == dto) {
			return null;
		}
		UserEntity user = modelMapper().map(dto, UserEntity.class);
		List<RoleEnum> rolesList = new ArrayList<>();
		rolesList.add(RoleEnum.valueOf(dto.getRole()));
		user.setRoles(rolesList);
		
		return user;
	}

	@Override
	public List<UserRegisterDTO> modelsToDtos(final List<UserEntity> models) {
		return models.stream().map(this::modelToDto).collect(Collectors.toList());
	}

	@Override
	public List<UserEntity> dtosToModels(final List<UserRegisterDTO> dtos) {
		return dtos.stream().map(this::dtoToModel).collect(Collectors.toList());
	}

}
