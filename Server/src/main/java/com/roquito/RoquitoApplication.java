package com.roquito;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.roquito.platform.messaging.ConnectionHandler;
import com.roquito.platform.messaging.persistence.MapDB;
import com.roquito.platform.notification.PusherService;
import com.roquito.platform.persistence.MongoDB;
import com.roquito.platform.service.UserService;

@SpringBootApplication
@EnableWebSocket
public class RoquitoApplication extends SpringBootServletInitializer implements WebSocketConfigurer {

    @Value("${app.environment}")
    private static String environment;
    
    @Value("${mapdb.filepath}")
    private static String mapdbFilePath;

    @Value("${mapdb.encryptedPassword}")
    private static String encryptedMapdbPassword;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
	registry.addHandler(roquitoWebSocketHandler(), "/rws").withSockJS();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	return application.sources(RoquitoApplication.class);
    }

    @Bean
    public WebSocketHandler roquitoWebSocketHandler() {
	return new ConnectionHandler();
    }

    @Bean
    public UserService userService() {
	return new UserService();
    }

    public static void main(String[] args) {
	SpringApplication app = new SpringApplication(RoquitoApplication.class);

	// Initialize MongoDB connection
	MongoDB.getInstance().initMongoDBConnection(false);
	//Start MapDB
	//MapDB.getInstance().initMapDB(mapdbFilePath, encryptedMapdbPassword);
	// Start pusher service
	PusherService.getInstance().start();
	
	app.run(args);
    }
}
