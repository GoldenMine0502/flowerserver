package kr.goldenmine.flowerserver;

import kr.goldenmine.flowerserver.file.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)
public class FlowerserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowerserverApplication.class, args);
    }

}
