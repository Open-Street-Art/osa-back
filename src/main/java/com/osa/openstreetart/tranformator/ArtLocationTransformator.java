package com.osa.openstreetart.tranformator;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.osa.openstreetart.dto.ArtLocationDTO;
import com.osa.openstreetart.entity.ArtEntity;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Component
@Transactional
public class ArtLocationTransformator implements AbstractTransformator<ArtEntity, ArtLocationDTO> {

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
	public ArtLocationDTO modelToDto(ArtEntity model) {
		if (null == model)
			return null;
		return modelMapper().map(model, ArtLocationDTO.class);
	}

	@Override
	public ArtEntity dtoToModel(ArtLocationDTO dto) {
		if (null == dto) {

		}
		return modelMapper().map(dto, ArtEntity.class);
	}

	@Override
	public List<ArtLocationDTO> modelsToDtos(List<ArtEntity> models) {
		return models.stream().map(this::modelToDto).collect(Collectors.toList());
	}

	@Override
	public List<ArtEntity> dtosToModels(List<ArtLocationDTO> dtos) {
		return dtos.stream().map(this::dtoToModel).collect(Collectors.toList());
	}
	
}
