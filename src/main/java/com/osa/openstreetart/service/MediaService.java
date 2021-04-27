package com.osa.openstreetart.service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.DocumentException;
import com.osa.openstreetart.entity.ArtEntity;
import com.osa.openstreetart.entity.CityEntity;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.repository.ArtRepository;
import com.osa.openstreetart.repository.CityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class MediaService {
	
	@Autowired
	private ArtRepository artRepo;

	@Autowired
	private CityRepository cityRepo;

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

	public HttpEntity<byte[]> sendPDF(Integer cityId) throws DocumentException, IOException, OSA404Exception {
		// Récupération des données
		Optional<CityEntity> city = cityRepo.findById(cityId);
		if (city.isEmpty())
			throw new OSA404Exception("City not found");
		Collection<ArtEntity> arts = artRepo.findByCityId(cityId);

		// Génération du PDF
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setOrder(0);

		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.addDialect(new Java8TimeDialect());
		templateEngine.setTemplateResolver(templateResolver);

		Context context = new Context();
		context.setVariable("city", city.get());
		context.setVariable("arts", arts);
		String html = templateEngine.process("pdf_template", context);

		// System.out.println(html);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(html);
		renderer.layout();
		renderer.createPDF(baos);
		byte[] pdfAsBytes = baos.toByteArray();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_PDF);
		header.set(
			HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=" + city.get().getName() + "_arts.pdf"
		);
		header.setContentLength(pdfAsBytes.length);
		return new HttpEntity<>(pdfAsBytes, header);
	}
}
