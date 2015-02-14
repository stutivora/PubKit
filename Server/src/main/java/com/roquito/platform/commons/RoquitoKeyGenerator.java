package com.roquito.platform.commons;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by puran on 2/7/15.
 */
public final class RoquitoKeyGenerator {

    private static final char[] symbols;

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (char ch = 'a'; ch <= 'z'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();
    }

    private final SecureRandom secureRandom = new SecureRandom();
    private final Random random = new Random();
    private char[] buf;

    /**
     * This works by choosing 130 bits from a cryptographically secure random bit generator,
     * and encoding them in base-32. 128 bits is considered to be cryptographically strong, but
     * each digit in a base 32 number can encode 5 bits, so 128 is rounded up to the next multiple
     * of 5. This encoding is compact and efficient, with 5 random bits per character*
     *
     * @return secure session id
     */
    public String getSecureSessionId() {
        return new BigInteger(130, secureRandom).toString(32);
    }

    public String getRandomKey() {
        return getRandomKey(32);
    }

    public String getRandomKey(int length) {
        buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}
