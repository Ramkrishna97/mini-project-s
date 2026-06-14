package com.example.chatreactive;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class ChatWebSocketHandler implements WebSocketHandler {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String userId;

    public ChatWebSocketHandler(String userId) {
        this.userId = userId;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        // INBOUND: read frames the browser sends, store them.
        // We subscribe separately — it must NOT be zipped with outbound,
        // because zip cancels everything the moment either side completes.
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(payload -> {
                    try {
                        JsonNode node    = mapper.readTree(payload);
                        String sender    = node.path("sender").asText(userId);
                        String receiver  = node.path("receiver").asText();
                        String content   = node.path("message").asText();
                        if (!content.isBlank() && !receiver.isBlank()) {
                            ChatService.storeMessage(sender, receiver, content);
                        }
                    } catch (Exception ignored) {}
                })
                .doOnError(e -> {}) // swallow connection-reset errors
                .subscribe();       // fire-and-forget — lives as long as the session

        // OUTBOUND: push every message addressed to this user.
        // The session closes naturally when the browser disconnects,
        // which cancels this flux automatically.
        return session.send(
                ChatService.getMessagesFor(userId)
                        .map(msg -> session.textMessage(msg.toJson()))
        );
    }
}