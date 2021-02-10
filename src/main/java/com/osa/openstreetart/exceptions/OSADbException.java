package com.osa.openstreetart.exceptions;

public class OSADbException extends OSAException {

	private static final long serialVersionUID = -2944221019260698225L;

	public OSADbException(final String message) {
		super(message);
	}

	public OSADbException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
