package com.example.ERPSpringBootBackEnd.configuration;
import com.example.ERPSpringBootBackEnd.dto.requestDto.*;
import com.example.ERPSpringBootBackEnd.services.DesignationService;
import com.example.ERPSpringBootBackEnd.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class Config {
    @Autowired
    private DesignationService designationService;
    @Bean
    CommandLineRunner run(UserService userService, DesignationService designationService) {
        return args -> {
            userService.save(new UserDto("Rehnuma", "Reza", "bakbik", "1234", "ADMIN"));
            userService.save(new UserDto("Rehnuma", "Reza", "bakbik22", "1234", "USER"));
            userService.save(
                    new UserDto(
                            "Salman",
                            "Khan",
                            "salman11",
                            "1234",
                            "USER",
                            "1991-01-02",
                            "Male",
                            "Islam",
                            new ContactInfoDto(
                                    "0293049",
                                    "abc@test.com",
                                    "test Address"),
                            null,
                            new JobProfileDto(
                                    "e-123",
                                    "Part-Time",
                                    "Senior",
                                    "2024-02-01",
                                    12000.0,
                                    1200,
                                    designationService.getAllDesignations().get(2)),
                            new ArrayList<>()
                    )
            );

        };
    }
}
