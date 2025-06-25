package org.yearup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
//@EnableMethodSecurity
public class EasyshopApplication {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java -jar app.jar <username> <password>");
            System.exit(1);
        }
// Set system properties with the username and password so Spring can read them later.
        System.setProperty("dbUsername", args[0]);
        System.setProperty("dbPassword", args[1]);

        SpringApplication.run(EasyshopApplication.class, args);
    }

}
