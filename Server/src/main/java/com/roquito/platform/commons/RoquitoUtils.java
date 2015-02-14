package com.roquito.platform.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by puran on 2/5/15.
 */
public final class RoquitoUtils {
    private static final Logger log = LoggerFactory.getLogger(RoquitoUtils.class);
    private static final String SALT = "R0qui!0Rocks";

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static String getPasswordHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String saltedPassword = SALT + password;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(saltedPassword.getBytes("UTF-8"));

        byte byteData[] = md.digest();

        // convert the byte to hex format
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static boolean comparePasswords(String userPassword, String hashedPassword) {
        boolean matches = false;
        try {
            String userHashPassword = getPasswordHash(userPassword);
            if (userHashPassword != null) {
                return userHashPassword.equals(hashedPassword);
            }
        } catch (Exception e) {
            log.error("Error comparing passwords", e);
        }
        return matches;
    }

    public static boolean hasValue(String value) {
        return value != null && !value.isEmpty();
    }
}
