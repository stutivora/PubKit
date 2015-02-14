package com.roquito.web.exception;

/**
 * Created by puran on 2/5/15.
 */
public class RoquitoServerException extends RuntimeException {

	private static final long serialVersionUID = 4805491609747396624L;

	public RoquitoServerException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
