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
package com.roquito.platform.messaging.protocol.mqtt.proto.parser;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;

import java.util.HashMap;
import java.util.Map;

import com.roquito.platform.messaging.protocol.mqtt.proto.messages.AbstractMessage;

/**
 * @author andrea
 */
public class MQTTDecoder {
    
    //3 = 3.1, 4 = 3.1.1
    static final AttributeKey<Integer> PROTOCOL_VERSION = AttributeKey.valueOf("version");
    
    private final Map<Byte, DemuxDecoder> m_decoderMap = new HashMap<>();
    
    public MQTTDecoder() {
       m_decoderMap.put(AbstractMessage.CONNECT, new ConnectDecoder());
       m_decoderMap.put(AbstractMessage.CONNACK, new ConnAckDecoder());
       m_decoderMap.put(AbstractMessage.PUBLISH, new PublishDecoder());
       m_decoderMap.put(AbstractMessage.PUBACK, new PubAckDecoder());
       m_decoderMap.put(AbstractMessage.SUBSCRIBE, new SubscribeDecoder());
       m_decoderMap.put(AbstractMessage.SUBACK, new SubAckDecoder());
       m_decoderMap.put(AbstractMessage.UNSUBSCRIBE, new UnsubscribeDecoder());
       m_decoderMap.put(AbstractMessage.DISCONNECT, new DisconnectDecoder());
       m_decoderMap.put(AbstractMessage.PINGREQ, new PingReqDecoder());
       m_decoderMap.put(AbstractMessage.PINGRESP, new PingRespDecoder());
       m_decoderMap.put(AbstractMessage.UNSUBACK, new UnsubAckDecoder());
       m_decoderMap.put(AbstractMessage.PUBCOMP, new PubCompDecoder());
       m_decoderMap.put(AbstractMessage.PUBREC, new PubRecDecoder());
       m_decoderMap.put(AbstractMessage.PUBREL, new PubRelDecoder());
    }

    public AbstractMessage decode(AttributeMap ctx, ByteBuf byteBuf) throws Exception {
        byteBuf.markReaderIndex();
        if (!Utils.checkHeaderAvailability(byteBuf)) {
            byteBuf.resetReaderIndex();
            return null;
        }
        byteBuf.resetReaderIndex();
        
        byte messageType = Utils.readMessageType(byteBuf);
        
        DemuxDecoder decoder = m_decoderMap.get(messageType);
        if (decoder == null) {
            throw new CorruptedFrameException("Can't find any suitable decoder for message type: " + messageType);
        }
        return decoder.decode(ctx, byteBuf);
    }
}
