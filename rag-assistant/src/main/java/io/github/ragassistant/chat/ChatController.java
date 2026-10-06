package io.github.ragassistant.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor(
                        request -> "Modèle: " + request.prompt().getOptions().getModel()
                                + " | Température: " + request.prompt().getOptions().getTemperature()
                                + " | Messages: " + request.prompt().getInstructions().size(),
                        response -> "Réponse: " + response.getResult().getOutput().getText(),
                        0
                ))
                .build();
        this.chatMemory = chatMemory;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .system("""
                        Tu es un mentor Java senior.
                        Réponds toujours en français, en 3 phrases maximum,
                        et termine par un exemple concret en Spring Boot.
                        """)
                .user(message)
                .call().content();
    }

    @GetMapping("/chat/memory")
    public String chatWithMemory(@RequestParam String conversationId
            , @RequestParam String message) {
        return chatClient.prompt()
                .advisors(
                        a -> a.advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                .param(ChatMemory.CONVERSATION_ID, conversationId)
                )
                .user(message)
                .call().content();
    }

    @GetMapping("/chat/temperature")
    public String withTemperature(@RequestParam String message,
                                  @RequestParam double temperature) {
        return chatClient.prompt()
                .user(message)
                .options(OllamaChatOptions.builder()
                        .temperature(temperature)
                        )
                .call()
                .content();
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
