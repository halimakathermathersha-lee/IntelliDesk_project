package com.intellidesk;

import com.intellidesk.entity.AppUser;
import com.intellidesk.entity.Role;
import com.intellidesk.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSetup {

    @Bean
    CommandLineRunner createAdmin(
            UserRepository users,
            PasswordEncoder encoder) {

        return args -> {

            String email = "admin@intellidesk.com";

            if (!users.existsByEmail(email)) {

                AppUser admin = new AppUser(
                        "IntelliDesk Admin",
                        email,
                        encoder.encode("Admin@12345"),
                        Role.ADMIN
                );

                users.save(admin);

                System.out.println("Admin account created successfully!");
            } else {
                System.out.println("Admin account already exists!");
            }
        };
    }
}