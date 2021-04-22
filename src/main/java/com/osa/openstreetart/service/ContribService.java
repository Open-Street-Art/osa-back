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

    public void saveExistingContrib(PostContribDTO contrib, UserEntity contribUser, Integer artId) throws  OSA400Exception {
		
		ContribEntity contribArt = new ContribEntity();
		Optional<ArtEntity> art = artRepo.findById(artId);
		if (!art.isPresent()) {
			throw new OSA400Exception("Art not found.");
		}

		contribArt.setName(contrib.getName());
		contribArt.setDescription(contrib.getDescription());
		contribArt.setArt(art.get());

		Collection<String> pictures = new ArrayList<>();
		if (contrib.getPicture1() != null && !contrib.getPicture1().isEmpty())
			pictures.add(contrib.getPicture1());
		if (contrib.getPicture2() != null && !contrib.getPicture2().isEmpty())
			pictures.add(contrib.getPicture2());
		if (contrib.getPicture3() != null && !contrib.getPicture3().isEmpty())
			pictures.add(contrib.getPicture3());
		contribArt.setPictures(pictures);

		contribArt.setCity(art.get().getCity());
		contribArt.setAuthorName(contrib.getAuthorName());

		contribArt.setLongitude(art.get().getLongitude());
		contribArt.setLatitude(art.get().getLatitude());

		contribArt.setContributor(contribUser);
		contribArt.setCreationDateTime(LocalDateTime.now());
		contribRepo.save(contribArt);
	}

	public void saveNewContrib(PostNewContribDTO contrib, UserEntity contribUser) throws OSA400Exception {
		ContribEntity contribArt = new ContribEntity();
		if (contrib.getName().isEmpty() || contrib.getDescription().isEmpty()) 
			throw new OSA400Exception("Empty content");

		contribArt.setName(contrib.getName());
		contribArt.setDescription(contrib.getDescription());

		Collection<String> pictures = new ArrayList<>();
		pictures.add(contrib.getPicture1());
		pictures.add(contrib.getPicture2());
		pictures.add(contrib.getPicture3());
		contribArt.setPictures(pictures);
		
		contribArt.setLongitude(contrib.getLongitude());
		contribArt.setLatitude(contrib.getLatitude());
		
		contribArt.setContributor(contribUser);
		contribArt.setCreationDateTime(LocalDateTime.now());
		contribArt.setAuthorName(contrib.getAuthorName());
		contribRepo.save(contribArt);
	}

	
	public void delete(Integer artId) throws OSA404Exception {
		Optional<ContribEntity> contrib = contribRepo.findById(artId);
		if(!contrib.isPresent()) {
			throw new OSA404Exception("Contribution not found.");
        }
		contribRepo.delete(contrib.get());
	}

	public ContribEntity getOrFail(Integer contribId) throws OSA400Exception{
		Optional<ContribEntity> contrib = contribRepo.findById(contribId);
		if (contrib.isEmpty())
			throw new OSA400Exception("Contribution not found.");

		 return contrib.get();
	}

	public Collection<ContribEntity> findByArtId(Integer artId) {
		return contribRepo.findByArtId(artId);
	}

	public Iterable<ContribEntity> findAll() {
		return contribRepo.findAll();
	}
}
