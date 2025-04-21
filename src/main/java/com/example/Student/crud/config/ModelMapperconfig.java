package com.example.Student.crud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;
@Configuration
public class ModelMapperconfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}

