package com.healthrx.bajaj;

import com.healthrx.bajaj.service.WebhookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BajajHiringApplication {

    public static void main(String[] args) {
        SpringApplication.run(BajajHiringApplication.class, args);
    }

    @Bean
    CommandLineRunner run(WebhookService webhookService) {
        return args -> {
            System.out.println("\n********************************************************");
            System.out.println("* Bajaj Hiring App — Startup webhook flow starting...");
            System.out.println("********************************************************\n");
            webhookService.performFlow();
            System.out.println("\n********************************************************");
            System.out.println("* Flow completed. Check target/solution.sql for the query.");
            System.out.println("********************************************************\n");
        };
    }
}
