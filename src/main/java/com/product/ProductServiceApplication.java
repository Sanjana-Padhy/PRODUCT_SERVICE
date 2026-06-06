package com.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableCaching 
@EnableAsync
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
	
	@Bean
	public WebMvcConfigurer webMvcConfigurer() {

	    return new WebMvcConfigurer() {

	        @Override
	        public void addResourceHandlers(
	                ResourceHandlerRegistry registry) {

	            registry.addResourceHandler("/uploads/**")
	                    .addResourceLocations(
	                        "file:uploads/");
	        }
	    };
	}

}
