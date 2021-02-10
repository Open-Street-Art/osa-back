package com.osa.openstreetart.exceptions;

public abstract class OSAException extends Exception {

	private static final long serialVersionUID = 1L;

	public OSAException(final String message) {
		super(message);
	}

	public OSAException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
