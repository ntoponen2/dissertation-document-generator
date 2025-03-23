package ru.ntoponen.dissertationdocumentgenerator.fw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.ntoponen.dissertationdocumentgenerator")
public class DissertationDocumentGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DissertationDocumentGeneratorApplication.class, args);
	}

}
