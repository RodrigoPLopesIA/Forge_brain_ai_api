package br.com.rodrigo.brainforge.services.AIResources.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

@Component
public class SpringAIClient implements AIClient {

    private final ChatClient chat;

    public SpringAIClient(ChatClient.Builder builder) {
        this.chat = builder
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .model("gpt-4.1-mini")
                                .build())
                .build();
    }

    @Override
    public String ask(String prompt) {
        int attempts = 0;
        int maxAttempts = 5;

        while (true) {
            try {
                return chat.prompt()
                        .user(prompt)
                        .call()
                        .content();

            } catch (Exception e) {
                String message = e.getMessage();

                // Detecta erro 429
                if (message != null && message.contains("rate_limit_exceeded")) {
                    attempts++;

                    if (attempts >= maxAttempts) {
                        throw new RuntimeException("Exceeded max retry attempts for rate limit", e);
                    }

                    // tempo de espera progressivo (backoff)
                    long wait = attempts * 1500L; // 1.5s → 3s → 4.5s → …

                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException ignored) {
                    }

                } else {
                    // outros erros devem explodir normalmente
                    throw e;
                }
            }
        }
    }
}