/*
 * Copyright (c) 2012-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package com.pubkit.platform.messaging.protocol.mqtt.proto;

import com.pubkit.platform.messaging.protocol.mqtt.proto.messages.AbstractMessage;

/**
 * Common utils methodd used in codecs.
 * @author andrea
 */
public class Utils {
    
     public static final int MAX_LENGTH_LIMIT = 268435455;
    /**
     * Return the number of bytes to encode the gicen remaining length value
     */
    static int numBytesToEncode(int len) {
        if (0 <= len && len <= 127) return 1;
        if (128 <= len && len <= 16383) return 2;
        if (16384 <= len && len <= 2097151) return 3;
        if (2097152 <= len && len <= 268435455) return 4;
        throw new IllegalArgumentException("value shoul be in the range [0..268435455]");
    }
    
    static byte encodeFlags(AbstractMessage message) {
        byte flags = 0;
        if (message.isDupFlag()) {
            flags |= 0x08;
        }
        if (message.isRetainFlag()) {
            flags |= 0x01;
        }
        
        flags |= ((message.getQos().ordinal() & 0x03) << 1);
        return flags;
    }

    /**
     * Converts MQTT message type to a textual description.
     * */
    public static String msgType2String(int type) {
        switch (type) {
            case AbstractMessage.CONNECT: return "CONNECT";
            case AbstractMessage.CONNACK: return "CONNACK";
            case AbstractMessage.PUBLISH: return "PUBLISH";
            case AbstractMessage.PUBACK: return "PUBACK";
            case AbstractMessage.PUBREC: return "PUBREC";
            case AbstractMessage.PUBREL: return "PUBREL";
            case AbstractMessage.PUBCOMP: return "PUBCOMP";
            case AbstractMessage.SUBSCRIBE: return "SUBSCRIBE";
            case AbstractMessage.SUBACK: return "SUBACK";
            case AbstractMessage.UNSUBSCRIBE: return "UNSUBSCRIBE";
            case AbstractMessage.UNSUBACK: return "UNSUBACK";
            case AbstractMessage.PINGREQ: return "PINGREQ";
            case AbstractMessage.PINGRESP: return "PINGRESP";
            case AbstractMessage.DISCONNECT: return "DISCONNECT";
            default: throw  new RuntimeException("Can't decode message type " + type);
        }
    }
}
