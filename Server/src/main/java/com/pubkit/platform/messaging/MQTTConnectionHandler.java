package com.pubkit.platform.messaging;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.local.LocalServerChannel;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com.pubkit.platform.messaging.protocol.mqtt.proto.messages.AbstractMessage;
import com.pubkit.platform.messaging.protocol.mqtt.proto.parser.MQTTDecoder;

public class MQTTConnectionHandler extends BinaryWebSocketHandler implements SubProtocolCapable {
    
    /* Only to use as an attribute map*/
    private LocalServerChannel ctx = new LocalServerChannel();
    
    private MQTTDecoder mqttDecoder = new MQTTDecoder();
    
    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        ByteBuf byteBuf = Unpooled.copiedBuffer(message.getPayload());
        AbstractMessage mqttMessage = mqttDecoder.decode(ctx, byteBuf);
    }
    

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }


    @Override
    public List<String> getSubProtocols() {
        List<String> mqttProtocolVersions = new ArrayList<String>();
        mqttProtocolVersions.add("mqttv3.1");
        mqttProtocolVersions.add("mqttv3.1.1");
        
        return mqttProtocolVersions;
    }
}
