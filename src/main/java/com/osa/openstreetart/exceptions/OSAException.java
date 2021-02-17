package com.osa.openstreetart.exceptions;

public abstract class OSAException extends Exception {

	private static final long serialVersionUID = 1L;

	protected OSAException(final String message) {
		super(message);
	}

	protected OSAException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
