package com.osa.openstreetart.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.osa.openstreetart.dto.ContribDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.ContribEntity;
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


	public void saveContrib(ContribEntity contrib) {
		// accepter la contribution
		contrib.setApproved(true);
		contribRepo.save(contrib);

		//l'art associé à la contribution
		ArtEntity art = contrib.getArt();
		art.setName(contrib.getName());
		art.setDescription(contrib.getDescription());

		// les images de la contribution
		Collection<String> pictures = new ArrayList<String>();
		
		//
		// for(String picture: art.getPictures())
		// {
		// 	pictures.add(picture);
		// }

		for(String picture: contrib.getPictures())
		{
			pictures.add(picture);
		}
		
		art.setPictures(pictures);
		artRepo.save(art);
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

		if (dto.getPicture1().isEmpty()) {
			throw new OSA400Exception("Picture 1 is empty.");
		}
		
		//l'oeuvre recevant la contribution
		Optional<ArtEntity> art = artRepo.findById(artId);

		if (!art.isPresent()) {
			throw new OSA400Exception("Art not found.");
		}

		//la contribution
		ContribEntity contribArt = new ContribEntity();
		contribArt.setName(dto.getName());
		contribArt.setDescription(dto.getDescription());
		contribArt.setArt(art.get());

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
		contribArt.setCity(art.get().getCity());

		//les coordonnées Géo
		contribArt.setLongitude(art.get().getLongitude());
		contribArt.setLatitude(art.get().getLatitude());
		
		contribArt.setCreationDateTime(LocalDateTime.now());
		contribArt.setApproved(false);
		
		return contribArt;
	}
	
    public void save(ContribDTO contrib2,UserEntity contribUser, Integer artId) throws  OSA400Exception{
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
