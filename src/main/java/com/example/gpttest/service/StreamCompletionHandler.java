package com.example.gpttest.service;

import com.example.gpttest.dto.GPTCompletionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.service.OpenAiService;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamCompletionHandler extends TextWebSocketHandler {

    private final HashMap<String, WebSocketSession> sessionHashMap;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OpenAiService openAiService;

    /* Client가 접속 시 호출되는 메서드 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        sessionHashMap.put(session.getId(), session);
        log.info("현재 접근한 유저 : {}", session.getId());
    }

    /* Client가 접속 해제 시 호출되는 메서드드 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        sessionHashMap.remove(session.getId());
        log.info("연결해제 한 유저 : {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        GPTCompletionRequest completionRequest = objectMapper.readValue(message.getPayload(),
                GPTCompletionRequest.class);

        sessionHashMap.keySet().forEach(key -> {
            streamCompletion(key, completionRequest, session);
        });
    }

    private void streamCompletion(String key, GPTCompletionRequest completionRequest, WebSocketSession session) {
        openAiService.streamChatCompletion(GPTCompletionRequest.of(completionRequest))
                .blockingForEach(completion -> {
                    sessionHashMap.get(key).sendMessage(
                            new TextMessage(toMessage(completion))
                    );
                    if ("stop".equals(completion.getChoices().get(0).getFinishReason())) {
                        session.close();
                    }
                });
    }

    private static String toMessage(ChatCompletionChunk completion) {
        String content = completion.getChoices().get(0).getMessage().getContent();
        if (content == null) {
            return "";
        }
        return content;
    }

}
