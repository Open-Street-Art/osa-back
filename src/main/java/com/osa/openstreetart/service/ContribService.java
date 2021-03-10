package com.osa.openstreetart.service;

import java.time.LocalDateTime;
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


	public void setArt(Integer artId, ContribDTO dto) throws OSA404Exception, OSA400Exception {
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

	private ContribEntity verifyContrib(ContribDTO dto) throws OSA400Exception {
		if (dto == null) {
			throw new OSA400Exception("Empty contribution");
        }
		if (dto.getId() == null) {
			throw new OSA400Exception("Empty contribution");
        }
        if (dto.getName().length() < 2) {
			throw new OSA400Exception("Name too short.");
        }
		if (dto.getDescription().isEmpty()) {
			throw new OSA400Exception("Description is empty");
		}
		if (artRepo.findById(dto.getArt().getId()).isPresent()) {
			throw new OSA400Exception("Art not found.");
		}
		if (dto.getPictures().isEmpty()) {
			throw new OSA400Exception("Pictures is Empty.");
        }
		if (userRepo.findByUsername(dto.getContributor().getUsername()).isEmpty()) {
			throw new OSA400Exception("Author not found.");
		}
		if (dto.getAuthorName().isEmpty()) {
			throw new OSA400Exception("Author name is Empty.");
		}
		if (dto.getApproved()) {
			throw new OSA400Exception("Contribution already approved.");
		}
		ContribEntity resetArt = new ContribEntity();
		resetArt.setId(dto.getId());
		resetArt.setName(dto.getName());
		resetArt.setDescription(dto.getDescription());
		resetArt.setArt(dto.getArt());
		resetArt.setPictures(dto.getPictures());
		resetArt.setContributor(dto.getContributor());
		resetArt.setCity(artRepo.findById(dto.getId()).get().getCity());
		resetArt.setLongitude(artRepo.findById(dto.getId()).get().getLongitude());
		resetArt.setLatitude(artRepo.findById(dto.getId()).get().getLatitude());
		resetArt.setApproved(dto.getApproved());
		return resetArt;
	}
	
    public void save(ContribDTO contrib2) throws OSA404Exception, OSA400Exception{
		ContribEntity contrib = verifyContrib(contrib2);
		contrib.setCreationDateTime(LocalDateTime.now());
		contribRepo.save(contrib);
	}


	public void delete(Integer artId) throws OSA404Exception {
		Optional<ContribEntity> contrib = contribRepo.findById(artId);
		if(!contrib.isPresent()) {
			throw new OSA404Exception("Contribution not found.");
        }
		contribRepo.delete(contrib.get());
	}
}
