package com.arilson.fitness.fitness_app_api.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String usuario;
    private String senha;
}
