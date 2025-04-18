package com.example.back_end;

import com.cloudinary.Cloudinary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableWebSecurity
@EnableJpaRepositories
public class TtstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(TtstoreApplication.class, args);
	}


	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Cloudinary getCloudinary(){
		Map config = new HashMap();
		config.put("cloud_name", "dwkmrgleh");
		config.put("api_key", "557369255376338");
		config.put("api_secret", "nIuE6-yeLORWDrQ_5RGN1DKzRGw");
		config.put("secure", true);
		return new Cloudinary(config);
	}

}
