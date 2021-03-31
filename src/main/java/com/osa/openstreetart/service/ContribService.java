package com.osa.openstreetart.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.osa.openstreetart.dto.PostContribDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
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

	@Autowired
	CityService cityService;

	public void acceptContrib(ContribEntity contrib) throws OSA400Exception {
		if(contrib.getApproved() != null)
			throw new OSA400Exception("This contribution has already been processed.");

		contrib.setApproved(true);
		contribRepo.save(contrib);

		ArtEntity art = contrib.getArt();
		art.setName(contrib.getName());
		art.setDescription(contrib.getDescription());
		Collection<String> newPictures = new ArrayList<>();
		for (String pic : contrib.getPictures())
			newPictures.add(pic);
		art.setPictures(newPictures);

		// On créé la ville si nécessaire puis on l'attribue a la contribution
		CityEntity city = cityService.getCityFromLatLong(
			art.getLatitude(),
			art.getLongitude()
		);

		if (city != null)
			art.setCity(cityService.registerCity(city));

		artRepo.save(art);
	}

	public void denyContrib(ContribEntity contrib) throws OSA400Exception {
		if(contrib.getApproved() != null)
			throw new OSA400Exception("This contribution has already been processed.");

		contrib.setApproved(false);
		contribRepo.save(contrib);
	}
	private ContribEntity verifyContrib(PostContribDTO dto, UserEntity contribUser, Integer artId) throws OSA400Exception {
		if (dto == null) {
			throw new OSA400Exception("Empty contribution");
        }
        if (dto.getName().length() < 2) {
			throw new OSA400Exception("Name too short.");
        }
		if (dto.getDescription().isEmpty()) {
			throw new OSA400Exception("Description is empty");
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
		Collection<String> pictures = new ArrayList<>();
		if (!dto.getPicture1().isEmpty())
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
		
		return contribArt;
	}
	
    public void save(PostContribDTO contrib2,UserEntity contribUser, Integer artId) throws  OSA400Exception{
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
