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
package com.pubkit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.pubkit.platform.messaging.MQTTConnectionHandler;
import com.pubkit.platform.messaging.PKMPConnectionHandler;
import com.pubkit.platform.service.QueueService;

/**
 * Created by puran
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableWebSocket
@Import(SwaggerConfig.class)
public class PubKitApplication extends SpringBootServletInitializer implements WebSocketConfigurer {

	@Autowired
	private QueueService queueService;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		PKMPConnectionHandler pkmpHandler = new PKMPConnectionHandler(queueService);

		DefaultHandshakeHandler pkmpHandShakeHandler = new DefaultHandshakeHandler();
		pkmpHandShakeHandler.setSupportedProtocols("pkmp");

		registry.addHandler(pkmpHandler, "/pkmp");
		registry.addHandler(pkmpHandler, "/pkmp-sj").setHandshakeHandler(pkmpHandShakeHandler).withSockJS();

		MQTTConnectionHandler mqttHandler = new MQTTConnectionHandler();

		DefaultHandshakeHandler handShakeHandler = new DefaultHandshakeHandler();
		handShakeHandler.setSupportedProtocols("mqttv3.1, mqttv3.1.1");
		registry.addHandler(mqttHandler, "/mqtt").setHandshakeHandler(handShakeHandler);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PubKitApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(PubKitApplication.class);
		app.run(args);
	}
}
