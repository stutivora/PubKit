package com.roquito.platform.commons;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by puran on 2/5/15.
 */
public final class RoquitoUtils {
    private static final Logger log = LoggerFactory.getLogger(RoquitoUtils.class);
    
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static String getPasswordHash(String password) {
	try {
	    return PasswordHash.createHash(password);
	} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
	    log.error("Error generating password hash", e);
	}
	return null;
    }

    public static boolean comparePasswords(String password, String passwordHash) {
        try {
	    return PasswordHash.validatePassword(password, passwordHash);
	} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
	    log.error("Error comparing password hash", e);
	}
        return false;
    }

    public static boolean hasValue(String value) {
        return value != null && !value.isEmpty();
    }
}
