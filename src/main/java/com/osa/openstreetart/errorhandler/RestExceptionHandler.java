package com.osa.openstreetart.errorhandler;

import org.springframework.core.annotation.Order;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityNotFoundException;

import com.osa.openstreetart.exceptions.OSA400Exception;
import com.osa.openstreetart.exceptions.OSA401Exception;
import com.osa.openstreetart.exceptions.OSA403Exception;
import com.osa.openstreetart.exceptions.OSA404Exception;
import com.osa.openstreetart.exceptions.OSA409Exception;
import com.osa.openstreetart.exceptions.OSA500Exception;
import com.osa.openstreetart.exceptions.OSAException;

import org.hibernate.exception.SQLGrammarException;
import org.springframework.core.Ordered;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return buildResponseEntity(new ErrorDto(""), status);
	}

	private ResponseEntity<Object> buildResponseEntity(final ErrorDto errorDto, final HttpStatus status) {
		return new ResponseEntity<>(errorDto, status);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleException(final EntityNotFoundException ex) {
		ErrorDto errorDto = new ErrorDto("ENF");
		log.error("ENF", ex);
		return buildResponseEntity(errorDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<Object> handleException(final IllegalArgumentException ex) {
		ErrorDto errorDto = new ErrorDto("IAE");
		log.error("IAE", ex);
		return buildResponseEntity(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(SQLGrammarException.class)
	protected ResponseEntity<Object> handleException(final SQLGrammarException ex) {
		ErrorDto errorDto = new ErrorDto("SGE");
		log.error("SGE", ex);
		return buildResponseEntity(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidDataAccessResourceUsageException.class)
	protected ResponseEntity<Object> handleException(final InvalidDataAccessResourceUsageException ex) {
		ErrorDto errorDto = new ErrorDto("IDARE");
		log.error("IDARE", ex);
		return buildResponseEntity(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(OSA404Exception.class)
	protected ResponseEntity<Object> handleException(final OSA404Exception ex) {
		ErrorDto errorDto = new ErrorDto(ex.getMessage());
		log.error(ex.getMessage(), ex);
		return buildResponseEntity(errorDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(OSA403Exception.class)
	protected ResponseEntity<Object> handleException(final OSA403Exception ex) {
		ErrorDto errorDto = new ErrorDto(ex.getMessage());
		log.error(ex.getMessage(), ex);
		return buildResponseEntity(errorDto, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(OSA401Exception.class)
	protected ResponseEntity<Object> handleException(final OSA401Exception ex) {
		ErrorDto errorDto = new ErrorDto(ex.getMessage());
		log.error(ex.getMessage(), ex);
		return buildResponseEntity(errorDto, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(OSA500Exception.class)
	protected ResponseEntity<Object> handleException(final OSA500Exception ex) {
		ErrorDto errorDto = new ErrorDto(ex.getMessage());
		log.error(ex.getMessage(), ex);
		return buildResponseEntity(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(OSA400Exception.class)
	protected ResponseEntity<Object> handleException(final OSA400Exception ex) {
		ErrorDto errorDto = new ErrorDto(ex.getMessage());
		log.error(ex.getMessage(), ex);
		return buildResponseEntity(errorDto, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(OSA409Exception.class)
	protected ResponseEntity<Object> handleException(final OSA409Exception ex) {
		ErrorDto errorDto = new ErrorDto(ex.getMessage());
		log.error(ex.getMessage(), ex);
		return buildResponseEntity(errorDto, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(OSAException.class)
	protected ResponseEntity<Object> handleException(final OSAException ex) {
		ErrorDto errorDto = new ErrorDto(ex.getMessage());
		return buildResponseEntity(errorDto, HttpStatus.MULTI_STATUS);
	}
	
}
