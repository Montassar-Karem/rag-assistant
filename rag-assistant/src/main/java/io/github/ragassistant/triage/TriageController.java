package io.github.ragassistant.triage;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TriageController {

    private final ChatClient chatClient;

    public TriageController(ChatClient.Builder builder,
                            @Value("classpath:prompts/triage-system.st") Resource systemPrompt) {
        this.chatClient = builder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @PostMapping("/triage")
    public EmailTriage triage(@RequestBody String email) {
        return chatClient.prompt()
                .user(email)
                .options(OllamaChatOptions.builder().temperature(0.0))
                .call()
                .entity(EmailTriage.class, spec -> spec
                        .useProviderStructuredOutput()   // Ollama constrains generation to the JSON schema
                        .validateSchema());              // Spring AI checks the result before converting it
    }
}