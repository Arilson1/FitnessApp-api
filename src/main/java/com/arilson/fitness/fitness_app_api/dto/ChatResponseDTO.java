package com.arilson.fitness.fitness_app_api.dto;

import lombok.Data;

@Data
public class ChatResponseDTO {
    private String message;
    private String threadId;

    public ChatResponseDTO(String response, String threadId) {
    }
}
