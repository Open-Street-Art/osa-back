package com.osa.openstreetart.tranformator;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.osa.openstreetart.dto.GetContribDTO;
import com.osa.openstreetart.entity.ContribEntity;
import com.osa.openstreetart.entity.UserEntity;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Component
@Transactional
public class ContribTransformator implements AbstractTransformator<ContribEntity, GetContribDTO> {

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
	public GetContribDTO modelToDto(ContribEntity model) {
		if (null == model)
			return null;

		GetContribDTO dto = modelMapper().map(model, GetContribDTO.class);
		UserEntity author;
		if ((author = model.getContributor()) != null)
			dto.setContributor_id(author.getId());
		return dto;
	}

	@Override
	public ContribEntity dtoToModel(GetContribDTO dto) {
		if (null == dto) {
			return null;
		}
		return modelMapper().map(dto, ContribEntity.class);
	}

	@Override
	public List<GetContribDTO> modelsToDtos(List<ContribEntity> models) {
		return models.stream().map(this::modelToDto).collect(Collectors.toList());
	}

	@Override
	public List<ContribEntity> dtosToModels(List<GetContribDTO> dtos) {
		return dtos.stream().map(this::dtoToModel).collect(Collectors.toList());
	}
	
}
