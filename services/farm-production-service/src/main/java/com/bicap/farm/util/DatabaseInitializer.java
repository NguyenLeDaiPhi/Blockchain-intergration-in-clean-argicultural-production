package com.bicap.farm.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> Farm Production Service dang khoi dong...");
    }
}