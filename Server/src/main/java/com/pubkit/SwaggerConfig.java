package com.pubkit;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

/**
 * Swagger configuration.
 */
@Configuration
@EnableSwagger
public class SwaggerConfig implements EnvironmentAware {

	private final Logger log = LoggerFactory.getLogger(SwaggerConfig.class);

	public static final String DEFAULT_INCLUDE_PATTERN = "/(api|applications)/.*?";

	private RelaxedPropertyResolver propertyResolver;

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment, "swagger.");
	}

	/**
	 * Swagger Spring MVC configuration.
	 */
	@Bean
	public SwaggerSpringMvcPlugin swaggerSpringMvcPlugin(SpringSwaggerConfig springSwaggerConfig) {
		log.debug("Starting Swagger");
		StopWatch watch = new StopWatch();
		watch.start();
		SwaggerSpringMvcPlugin swaggerSpringMvcPlugin = new SwaggerSpringMvcPlugin(springSwaggerConfig).apiInfo(apiInfo())
				.directModelSubstitute(Date.class, String.class).genericModelSubstitutes(ResponseEntity.class)
				.includePatterns(DEFAULT_INCLUDE_PATTERN);

		swaggerSpringMvcPlugin.build();
		watch.stop();
		log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
		return swaggerSpringMvcPlugin;
	}

	/**
	 * API Info as it appears on the swagger-ui page.
	 */
	private ApiInfo apiInfo() {
		return new ApiInfo(propertyResolver.getProperty("title"), propertyResolver.getProperty("description"),
				propertyResolver.getProperty("termsOfServiceUrl"), propertyResolver.getProperty("contact"), propertyResolver.getProperty("license"),
				propertyResolver.getProperty("licenseUrl"));
	}
}
