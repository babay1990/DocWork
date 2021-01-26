package com.shpaginWork.docWork;

import com.shpaginWork.docWork.storage.StorageProperties;
import com.shpaginWork.docWork.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DocWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocWorkApplication.class, args);
	}
	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}


}
