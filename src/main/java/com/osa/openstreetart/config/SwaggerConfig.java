package com.osa.openstreetart.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
    public Docket apiDocs() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .globalOperationParameters(
                        Collections.singletonList(
                                new ParameterBuilder().name("Authorization")
                                        .description("Authorization details for security (JWT token or BasicAuth)")
                                        .modelRef(new ModelRef("String"))
                                        .parameterType("header").required(false).build()));
    };

	@Bean
	public ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title("OpenStreetArt API")
			.description("OpenStreetArt API Documentation")
			.version("0.1")
			.build();
	}
}
