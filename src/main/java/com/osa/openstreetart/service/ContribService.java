package com.osa.openstreetart.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.osa.openstreetart.dto.PostContribDTO;
import com.osa.openstreetart.dto.PostNewContribDTO;
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

		ArtEntity art;
		if ((art = contrib.getArt()) == null) {
			art = new ArtEntity();
			if (contrib.getAuthorName().isEmpty())
				art.setAuthor(contrib.getContributor());
			else
				art.setAuthorName(contrib.getAuthorName());
			
			art.setLatitude(contrib.getLatitude());
			art.setLongitude(contrib.getLongitude());
			art.setCreationDateTime(LocalDateTime.now());
		}
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

    public void save(PostContribDTO contrib1, PostNewContribDTO contrib2, UserEntity contribUser, Integer artId) throws  OSA400Exception{
		
		Optional<ArtEntity> art;
		//la contribution qui sera enregistreé
		ContribEntity contribArt = new ContribEntity();

		// contribution modifiant une oeuvre existante
		if	(contrib1 != null) {
			// l'oeuvre recevant la contribution
			art = artRepo.findById(artId);
			if (!art.isPresent()) {
				throw new OSA400Exception("Art not found.");
			}

			contribArt.setName(contrib1.getName());
			contribArt.setDescription(contrib1.getDescription());
			contribArt.setArt(art.get());

			// les images de la contribution
			Collection<String> pictures = new ArrayList<>();
			if (!contrib1.getPicture1().isEmpty())
				pictures.add(contrib1.getPicture1());
			if (!contrib1.getPicture2().isEmpty())
				pictures.add(contrib1.getPicture2());
			if (!contrib1.getPicture3().isEmpty())
				pictures.add(contrib1.getPicture3());
			contribArt.setPictures(pictures);

			//la ville
			contribArt.setCity(art.get().getCity());

			//les coordonnées Géo
			contribArt.setLongitude(art.get().getLongitude());
			contribArt.setLatitude(art.get().getLatitude());
		}
		
		//nouvelle contribution
		if(contrib2 != null) {
			if (contrib2.getName().isEmpty() || contrib2.getDescription().isEmpty()) 
				throw new OSA400Exception("Empty content");

			contribArt.setName(contrib2.getName());
			contribArt.setDescription(contrib2.getDescription());

			// les images de la contribution
			Collection<String> pictures = new ArrayList<>();
			pictures.add(contrib2.getPicture1());
			pictures.add(contrib2.getPicture2());
			pictures.add(contrib2.getPicture3());
			contribArt.setPictures(pictures);
			
			contribArt.setAuthorName(contrib2.getAuthorName());
			
			//les coordonnées Géo
			contribArt.setLongitude(contrib2.getLongitude());
			contribArt.setLatitude(contrib2.getLatitude());
		}
		//le contributeur
		contribArt.setContributor(contribUser);

		contribArt.setCreationDateTime(LocalDateTime.now());

		contribRepo.save(contribArt);
	}

	
	public void delete(Integer artId) throws OSA404Exception {
		Optional<ContribEntity> contrib = contribRepo.findById(artId);
		if(!contrib.isPresent()) {
			throw new OSA404Exception("Contribution not found.");
        }
		contribRepo.delete(contrib.get());
	}
}
