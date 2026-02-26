package soyosa.inventaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class InventaireApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventaireApplication.class, args);
    }

}
