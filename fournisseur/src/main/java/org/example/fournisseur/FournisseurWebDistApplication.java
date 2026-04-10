package org.example.fournisseur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FournisseurWebDistApplication {

    public static void main(String[] args) {
        SpringApplication.run(FournisseurWebDistApplication.class, args);
    }

}
