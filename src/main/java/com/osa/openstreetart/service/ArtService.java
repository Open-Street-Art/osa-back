package com.osa.openstreetart.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

import com.osa.openstreetart.dto.ArtDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtService {
	
	@Autowired
	ArtRepository artRepo;

	@Autowired
	UserRepository userRepo;

	public void patch(Integer artId, ArtDTO dto) throws OSA404Exception, OSA400Exception {
		Optional<ArtEntity> optArt = artRepo.findById(artId);
		if (!optArt.isPresent())
			throw new OSA404Exception("Art not found.");

		if (dto.getName().length() < ArtEntity.NAME_MIN_LENGTH)
			throw new OSA400Exception("Name too short.");

		if (dto.getPicture1().isEmpty())
			throw new OSA400Exception("Picture 1 is empty.");

		if (dto.getAuthor().isEmpty() && dto.getAuthor_id() == null)
			throw new OSA400Exception("Author or author_id must be filled.");

		if (!dto.getAuthor().isEmpty() && dto.getAuthor_id() != null)
			throw new OSA400Exception("Author or author_id must be empty.");

		ArtEntity art = optArt.get();
		art.setName(dto.getName());
		art.setDescription(dto.getDescription());
		
		// Enregistrement des images en tableau de bytes
		Collection<byte[]> pictures = new ArrayList<byte[]>();
		pictures.add(Base64.getDecoder().decode(dto.getPicture1()));
		if (!dto.getPicture2().isEmpty())
			pictures.add(Base64.getDecoder().decode(dto.getPicture2()));
		if (!dto.getPicture3().isEmpty())
			pictures.add(Base64.getDecoder().decode(dto.getPicture3()));
		art.setPictures(pictures);
		
		if (!dto.getAuthor().isEmpty())
			art.setAuthorName(dto.getAuthor());
		else {
			Optional<UserEntity> optAuthor = userRepo.findById(dto.getAuthor_id());
			if (!optAuthor.isPresent() || !optAuthor.get().getRoles().contains(RoleEnum.ROLE_ARTIST))
				throw new OSA400Exception("Invalid author ID.");

			art.setAuthor(optAuthor.get());
		}
		artRepo.save(art);
	}

}
