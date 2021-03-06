package com.osa.openstreetart.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.List;

import com.osa.openstreetart.dto.ArtDTO;
import com.osa.openstreetart.dto.EditArtDTO;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.entity.RoleEnum;
import com.osa.openstreetart.entity.UserEntity;
import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.CityRepository;
import com.osa.openstreetart.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class ArtService {
	
	@Autowired
	ArtRepository artRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	CityRepository cityRepo;

	@Autowired
	CityService cityService;

	public void patch(Integer artId, EditArtDTO dto) throws OSA404Exception, OSA400Exception {
		if (dto == null)
			throw new OSA400Exception("empty values");
			
		Optional<ArtEntity> optArt = artRepo.findById(artId);
		if (optArt.isEmpty())
			throw new OSA404Exception("Art not found.");
		if (dto.getName().length() < ArtEntity.NAME_MIN_LENGTH)
			throw new OSA400Exception("Name too short.");

		if (dto.getPicture1().isEmpty())
			throw new OSA400Exception("Picture 1 is empty.");

		if (dto.getAuthor().isEmpty() && dto.getAuthorId() == null)
			throw new OSA400Exception("Author or author_id must be filled.");

		if (!dto.getAuthor().isEmpty() && dto.getAuthorId() != null)
			throw new OSA400Exception("Author or author_id must be empty.");

		ArtEntity art = optArt.get();
		art.setName(dto.getName());
		art.setDescription(dto.getDescription());
		
		// Enregistrement des images en tableau de bytes
		Collection<String> pictures = new ArrayList<>();
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
			Optional<UserEntity> optAuthor = userRepo.findById(dto.getAuthorId());
			if (optAuthor.isEmpty() || !optAuthor.get().getRoles().contains(RoleEnum.ROLE_ARTIST))
				throw new OSA400Exception("Invalid author ID.");

			art.setAuthor(optAuthor.get());
		}
		artRepo.save(art);
	}

	public void save(ArtDTO artDTO) throws OSA400Exception{
		
		ArtEntity art = validateArt(artDTO);
		art.setCreationDateTime(LocalDateTime.now());
		artRepo.save(art);
	}

	private ArtEntity validateArt(ArtDTO dto) throws OSA400Exception {
		if (dto.getAuthor().isEmpty() && dto.getAuthorId() == null
			|| !dto.getAuthor().isEmpty() && dto.getAuthorId() != null)
			throw new OSA400Exception("Either author or authorId must be filled.");
		if (dto.getName().length() < ArtEntity.NAME_MIN_LENGTH)
			throw new OSA400Exception("Name too short.");
		if (dto.getPicture1().isEmpty())
			throw new OSA400Exception("Picture 1 is empty.");

		var newArt = new ArtEntity();
		newArt.setName(dto.getName());
		newArt.setDescription(dto.getDescription());
		newArt.setLongitude(dto.getLongitude());
		newArt.setLatitude(dto.getLatitude());
		
		// Enregistrement des images en tableau de bytes
		Collection<String> pictures = new ArrayList<>();
		pictures.add(dto.getPicture1());
		pictures.add(dto.getPicture2());
		pictures.add(dto.getPicture3());
		newArt.setPictures(pictures);

		// Si un nom d'artiste est spécifié
		if (!dto.getAuthor().isEmpty())
			newArt.setAuthorName(dto.getAuthor());
		else {
			Optional<UserEntity> optAuthor = userRepo.findById(dto.getAuthorId());
			if (optAuthor.isEmpty() || !optAuthor.get().getRoles().contains(RoleEnum.ROLE_ARTIST))
				throw new OSA400Exception("Invalid author ID.");

				newArt.setAuthor(optAuthor.get());
		}
		
		// On récupère une possible ville
		CityEntity city = cityService.getCityFromLatLong(
			dto.getLatitude(),
			dto.getLongitude()
		);

		if (city != null) {
			CityEntity cityToLink = cityService.registerCity(city);
			if (cityToLink != null) {
				newArt.setCity(cityToLink);
			}
		}

		return newArt;
	}

	public void delete(Integer artId) throws OSA404Exception {
		Optional<ArtEntity> deletedArt = artRepo.findById(artId);
		if(deletedArt.isEmpty())
			throw new OSA404Exception("Art not found");
		
		artRepo.delete(deletedArt.get());
	}
	
	public ArtEntity getArt(Integer artId) {
		Optional<ArtEntity> art =  artRepo.findById(artId);
		if (art.isEmpty())
			return null;
		
		return art.get(); 
	} 

	public Collection<ArtEntity> findByCityId(Integer cityId) {
		return artRepo.findByCityId(cityId);
	}


	public ArtEntity getOrFail(Integer artId) throws OSA400Exception{
		Optional<ArtEntity> art = artRepo.findById(artId);
		if (art.isEmpty())
			throw new OSA400Exception("Art not found.");

		 return art.get();
	}

    public Collection<ArtEntity> findByNameWithSub(String content) {
        return artRepo.findByNameWithSub(content);
    }

    public Collection<ArtEntity> findByAuthorNameWithSub(String username) {
        return artRepo.findByAuthorNameWithSub(username);
    }

    public Collection<ArtEntity> findByAuthorId(Integer id) {
        return artRepo.findByAuthorId(id);
    }

	public Collection<ArtEntity> findByCitiesName(String content) {
		return artRepo.findByCitiesName(content);
	}

	public List<ArtEntity> findAll() {

		List<ArtEntity> arts = new ArrayList<>();
		Iterable<ArtEntity> iterable = artRepo.findAll();
		iterable.forEach(arts::add);
		
		return arts;
	}
}
