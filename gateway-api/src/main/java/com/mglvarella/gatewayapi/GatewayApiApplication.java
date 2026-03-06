package com.mglvarella.gatewayapi;

import com.mglvarella.gatewayapi.auth.domain.model.User;
import com.mglvarella.gatewayapi.auth.infrastructure.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class GatewayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApiApplication.class, args);
    }
    @Bean
    CommandLineRunner initDatabase(UserRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.findByUsername("usuario").isEmpty()) {
                User defaultUser = new User();
                defaultUser.setUsername("usuario");

                defaultUser.setPassword(passwordEncoder.encode("senha123"));

                defaultUser.setRoles(List.of("USER"));

                repository.save(defaultUser);
                System.out.println("Semente de dados: Usuário padrão criado com sucesso!");
            }
        };
    }

}
