package com.osa.openstreetart.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.osa.openstreetart.dto.ContribDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.ContribEntity;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.ContribRepository;
import com.osa.openstreetart.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContribService {

    @Autowired
	ContribRepository contribRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	ArtRepository artRepo;


	public void setArt(Integer artId, ContribEntity dto) throws OSA404Exception, OSA400Exception {
		if (dto == null) {
			throw new OSA400Exception("empty values");
        }
		Optional<ArtEntity> newArt = artRepo.findById(artId);
		if (!newArt.isPresent()) {
			throw new OSA404Exception("Art not found.");
        }
		if (dto.getName().length() < 2) {
			throw new OSA400Exception("Name too short.");
        }
		if (dto.getDescription().isEmpty()) {
			throw new OSA400Exception("Description is empty.");
        }
		if (dto.getPictures().isEmpty()) {
			throw new OSA400Exception("Pictures are empty.");
        }
		newArt.get().setName(dto.getName());
		newArt.get().setDescription(dto.getDescription());
		newArt.get().setPictures(dto.getPictures());
		
		// Si un nom d'artiste est spécifié
		if (dto.getAuthorName().isEmpty()) {
			newArt.get().setAuthorName(dto.getAuthorName());
		}
		else {
			Optional<UserEntity> optAuthor = userRepo.findByUsername(dto.getAuthorName());
			if (!optAuthor.isPresent() || !optAuthor.get().getRoles().contains(RoleEnum.ROLE_ARTIST)) {
				throw new OSA400Exception("Invalid author ID.");
			}
			newArt.get().setAuthor(optAuthor.get());
		}
		artRepo.save(newArt.get());
	}

	private ContribEntity verifyContrib(ContribDTO dto,UserEntity contribUser, Integer artId) throws OSA400Exception {
		if (dto == null) {
			throw new OSA400Exception("Empty contribution");
        }
        if (dto.getName().length() < 2) {
			throw new OSA400Exception("Name too short.");
        }
		if (dto.getDescription().isEmpty()) {
			throw new OSA400Exception("Description is empty");
		}

		if (artRepo.findById(artId).isPresent()) {
			throw new OSA400Exception("Art not found.");
		}
		if (dto.getPicture1().isEmpty())
			throw new OSA400Exception("Picture 1 is empty.");

		//l'oeuvre recevant la contribution
		ArtEntity art = artRepo.findById(artId).get();

		//la contribution
		ContribEntity contribArt = new ContribEntity();
		contribArt.setName(dto.getName());
		contribArt.setDescription(dto.getDescription());
		contribArt.setArt(art);

		// les images de la contribution
		Collection<String> pictures = new ArrayList<String>();
		pictures.add(dto.getPicture1());
		if (!dto.getPicture2().isEmpty())
			pictures.add(dto.getPicture2());
		if (!dto.getPicture3().isEmpty())
			pictures.add(dto.getPicture3());
		contribArt.setPictures(pictures);

		//le contributeur
		contribArt.setContributor(contribUser);

		//la ville
		contribArt.setCity(art.getCity());

		//les coordonnées Géo
		contribArt.setLongitude(art.getLongitude());
		contribArt.setLatitude(art.getLatitude());
		
		contribArt.setCreationDateTime(LocalDateTime.now());
		contribArt.setApproved(false);
		return contribArt;
	}
	
    public void save(ContribDTO contrib2,UserEntity contribUser, Integer artId) throws OSA404Exception, OSA400Exception{
		contribRepo.save(verifyContrib(contrib2, contribUser, artId));
	}


	public void delete(Integer artId) throws OSA404Exception {
		Optional<ContribEntity> contrib = contribRepo.findById(artId);
		if(!contrib.isPresent()) {
			throw new OSA404Exception("Contribution not found.");
        }
		contribRepo.delete(contrib.get());
	}
}
