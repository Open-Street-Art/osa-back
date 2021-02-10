package com.osa.openstreetart.exceptions;

public class OSA400Exception extends OSAException {

	private static final long serialVersionUID = -2944221019260698225L;

	public OSA400Exception(final String message) {
		super(message);
	}

	public OSA400Exception(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
