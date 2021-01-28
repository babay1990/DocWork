package com.shpaginWork.docWork;

;
import com.shpaginWork.docWork.storage.StorageProperties;
import com.shpaginWork.docWork.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.File;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DocWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocWorkApplication.class, args);

		File dir = new File(System.getProperty("catalina.home")+ "/" + "files");
		dir.mkdir();


	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}
}