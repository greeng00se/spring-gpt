package com.example.gpttest.dto;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GPTCompletionRequest {

    private String model;

    private String prompt;

    public static ChatCompletionRequest of(GPTCompletionRequest restRequest) {
        return ChatCompletionRequest.builder()
                .model(restRequest.getModel())
                .messages(List.of(new ChatMessage("user", restRequest.getPrompt())))
                .build();
    }
}
