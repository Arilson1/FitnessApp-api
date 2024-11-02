package com.arilson.fitness.fitness_app_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageDTO {
    private String content;
    private String threadId;
}
