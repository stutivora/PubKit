/* Copyright (c) 2015 32skills Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.roquito.platform.commons;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by puran
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
