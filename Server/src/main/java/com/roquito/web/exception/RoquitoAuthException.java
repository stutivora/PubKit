package com.roquito.web.exception;

/**
 * Created by puran on 2/5/15.
 */
public class RoquitoAuthException extends RuntimeException {
	
	private static final long serialVersionUID = 3518668985341033639L;

	public RoquitoAuthException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
