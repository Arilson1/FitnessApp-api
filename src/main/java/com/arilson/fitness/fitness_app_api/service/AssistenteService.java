package com.arilson.fitness.fitness_app_api.service;

import com.arilson.fitness.fitness_app_api.dto.AssistenteDTO;
import com.arilson.fitness.fitness_app_api.dto.ChatMessageDTO;
import com.arilson.fitness.fitness_app_api.dto.ChatResponseDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssistenteService {
    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String BASE_URL = "https://api.openai.com/v1";

    private String assistenteId;

    @PostConstruct
    public void init() {
        this.assistenteId = createAssistente();
    }

    public ChatResponseDTO chat(@NotNull ChatMessageDTO chatMessage) {
        String threadId = chatMessage.getThreadId();

        if (threadId == null || threadId.isEmpty()) {
            threadId = createThread();
        }

        addMessage(threadId, chatMessage.getContent());

        String response = runAssistenteAndAwaitResponse(threadId, this.assistenteId);

        return new ChatResponseDTO(response, threadId);
    }

    public String createAssistente() {
        HttpHeaders headers = createHeaders();
        Map<String, Object> request = new HashMap<>();

        String instructions = "Você é um assistente personalizado de treino e dieta. "
                + "Colete as seguintes informações do usuário: "
                + "1. Relatório Diário: Alimentação detalhada do dia e se realizou treino ou não. "
                + "2. Perfil do Usuário: Idade, altura, peso atual e desejado, gênero, nível de atividade diária. "
                + "3. Objetivos: Perda de gordura, ganho de massa muscular, melhora da performance em exercícios específicos, etc. "
                + "4. Restrições e Preferências Alimentares: Alergias, dietas específicas, alimentos que não gosta. "
                + "5. Histórico de Saúde: Condições médicas relevantes, lesões anteriores, medicações em uso. "
                + "6. Nível de Experiência em Treinos: Iniciante, intermediário ou avançado. "
                + "7. Suplementação: Suplementos em uso e interesse em iniciar suplementação. "
                + "8. Preferências de Treino: Tipo de exercícios preferidos e acesso a equipamentos. "
                + "9. Monitoramento de Progresso: Medidas corporais e resultados de exames recentes. "
                + "Com base nas informações fornecidas, ofereça análises nutricionais personalizadas, orientações de treino, acompanhamento de progresso e muito mais.";

        request.put("name", "Assistente Personalizado de Treino e Dieta");
        request.put("instructions", instructions);
        request.put("model", "gpt-4o");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + "/assistants",
                HttpMethod.POST,
                entity,
                Map.class
        );

        return (String) response.getBody().get("id");
    }

    private String createThread() {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + "/threads",
                HttpMethod.POST,
                entity,
                Map.class
        );

        return (String) response.getBody().get("id");
    }

    private void addMessage(String threadId, String content) {
        HttpHeaders headers = createHeaders();

        Map<String, Object> request = new HashMap<>();
        request.put("role", "user");
        request.put("content", content);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        restTemplate.exchange(
                BASE_URL + "/threads/" + threadId + "/messages",
                HttpMethod.POST,
                entity,
                Map.class
        );
    }

    public String runAssistenteAndAwaitResponse(String threadId, String assistenteId) {
        HttpHeaders headers = createHeaders();

        Map<String, String> request = Map.of("assistant_id", assistenteId);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> runResponse = restTemplate.exchange(
                BASE_URL + "/threads/" + threadId + "/runs",
                HttpMethod.POST,
                entity,
                Map.class
        );

        String runId = (String) runResponse.getBody().get("id");

        waitForCompletion(threadId, runId);

        return getLastMessage(threadId);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.add("OpenAI-Beta", "assistants=v2");
        return headers;
    }

    private void waitForCompletion(String threadId, String runId) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String status;

        do {
            try {
                Thread.sleep(1000);
                ResponseEntity<Map> response = restTemplate.exchange(
                    BASE_URL + "/threads/" + threadId + "/runs/" + runId,
                    HttpMethod.GET,
                    entity,
                    Map.class
                );

                status = (String) response.getBody().get("status");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrompido enquanto aguardava resposta", e);
            }
        } while (status.equals("in_progress") || status.equals("queued"));
    }

    @SuppressWarnings("unchecked")
    private String getLastMessage(String threadId) {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + "/threads/" + threadId + "/messages",
                HttpMethod.GET,
                entity,
                Map.class
        );

        List<Map<String, Object>> messages = (List<Map<String, Object>>) response.getBody().get("data");

        if (messages != null && !messages.isEmpty()) {
            return (String) messages.get(messages.size() - 1).get("content");
        }

        return "Não foi possível obter uma resposta.";
    }
}
