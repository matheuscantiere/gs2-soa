package br.com.fiap3ess.rotaseca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RotaSecaApplication {
    public static void main(String[] args) {
        SpringApplication.run(RotaSecaApplication.class, args);
    }
}
