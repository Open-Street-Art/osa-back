package com.osa.openstreetart.service;

import java.util.Optional;

import com.osa.openstreetart.dto.ContribDTO;
import com.osa.openstreetart.entity.ContribEntity;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.repository.CityRepository;
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
	CityRepository cityRepo;

    /**public void patch(Integer artId, ContribDTO dto) throws OSA404Exception, OSA400Exception {
		if (dto == null) {
			throw new OSA400Exception("empty values");
        }
		Optional<ContribEntity> optContrib = contribRepo.findById(artId);
		if (!optContrib.isPresent()) {
			throw new OSA404Exception("Art not found.");
        }
		if (dto.getName().length() < 2) {
			throw new OSA400Exception("Name too short.");
        }
		if (dto.getPictures().isEmpty()) {
			throw new OSA400Exception("No picture found.");
        }
		optContrib.get().setName(dto.getName());
		optContrib.get().setDescription(dto.getDescription());
		
		// Enregistrement des images en tableau de bytes
		Collection<String> pictures = new ArrayList<String>();
		pictures.add(dto.getPicture1());
		if (!dto.getPicture2().isEmpty())
			pictures.add(dto.getPicture2());
		if (!dto.getPicture3().isEmpty())
			pictures.add(dto.getPicture3());
		art.setPictures(pictures);
		
		// Si un nom d'artiste est spécifié
		if (!dto.getAuthor().isEmpty())
			art.setAuthorName(dto.getAuthor());
		else {
			Optional<UserEntity> optAuthor = userRepo.findById(dto.getAuthor_id());
			if (!optAuthor.isPresent() || !optAuthor.get().getRoles().contains(RoleEnum.ROLE_ARTIST))
				throw new OSA400Exception("Invalid author ID.");

			art.setAuthor(optAuthor.get());
		}
		artRepo.save(art);
	}**/

	private ContribEntity verifyContrib(ContribDTO dto) throws OSA404Exception, OSA400Exception {
		if (dto == null) {
			throw new OSA400Exception("Empty contribution");
        }
        if (dto.getName().length() < 2) {
			throw new OSA400Exception("Name too short.");
        }
        if (dto.getPicture1().isEmpty()) {
			throw new OSA400Exception("Picture 1 is empty.");
        }
		if (dto.getAuthor().isEmpty() && dto.getAuthor_id() == null) {
			throw new OSA400Exception("Author or author_id must be filled.");
        }
		if (!dto.getAuthor().isEmpty() && dto.getAuthor_id() != null) {
			throw new OSA400Exception("Author or author_id must be empty.");
        }
		ContribEntity resetArt = new ContribEntity();
		resetArt.setName(dto.getName());
		resetArt.setDescription(dto.getDescription());
		
        // Enregistrement des images
        resetArt.setPictures(dto.getPictures());
		return resetArt; 
	}

    public void save(ContribDTO contribDTO) throws OSA404Exception, OSA400Exception{
		ContribEntity contrib = verifyContrib(contribDTO);
		contrib.setCreationDateTime(LocalDateTime.now());
		ContribRepo.save(contrib);
	}

	public void delete(Integer artId) throws OSA404Exception {
		Optional<ContribEntity> deletedContrib = contribRepo.findById(artId);
		if(!deletedContrib.isPresent()) {
			throw new OSA404Exception("Contribution not found.");
        }
		contribRepo.delete(deletedContrib.get());
	}
}
