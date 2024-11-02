package com.arilson.fitness.fitness_app_api.controller;

import com.arilson.fitness.fitness_app_api.dto.AssistenteDTO;
import com.arilson.fitness.fitness_app_api.dto.ChatMessageDTO;
import com.arilson.fitness.fitness_app_api.dto.ChatResponseDTO;
import com.arilson.fitness.fitness_app_api.service.AssistenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assistente")
public class AssistenteController {

    @Autowired
    private AssistenteService assistenteService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatMessageDTO chatMessage) {
        return ResponseEntity.ok(assistenteService.chat(chatMessage));
    }
}
