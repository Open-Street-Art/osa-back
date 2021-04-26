package com.osa.openstreetart.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.repository.ArtRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

@Service
public class MediaService {
	
	@Autowired
	private ArtRepository artRepo;

	public void sendCSV(HttpServletResponse response, Integer cityId) throws IOException {
		response.setContentType("text/csv");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=toto.csv";
		response.setHeader(headerKey, headerValue);

		ICsvBeanWriter csvWriter = new CsvBeanWriter(
			response.getWriter(),
			CsvPreference.STANDARD_PREFERENCE
		);
		String[] csvHeader = {"Name", "Description", "Author", "Creation Date", "Longitude", "Latitude"};
		String[] nameMapping = {"name", "description", "authorName", "creationDateTime", "longitude", "latitude"};
		csvWriter.writeHeader(csvHeader);

		for (ArtEntity art : artRepo.findByCityId(cityId)) {
			csvWriter.write(art, nameMapping);
		}
		csvWriter.close();
	}

}
