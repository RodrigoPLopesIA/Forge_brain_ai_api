package br.com.rodrigo.brainforge.services.AIResources.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class SpringAIClient implements AIClient {

    private final ChatClient chat;

    public SpringAIClient(ChatClient.Builder builder) {
        this.chat = builder.build();
    }

    @Override
    public String ask(String prompt) {
        return chat.prompt().user(prompt).call().content();
    }
}