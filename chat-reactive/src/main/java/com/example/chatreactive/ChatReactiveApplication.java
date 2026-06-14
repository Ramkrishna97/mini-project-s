package com.example.chatreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;

@SpringBootApplication
public class ChatReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatReactiveApplication.class, args);
    }

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> urlMap = Map.of(
                "/ws/user1", new ChatWebSocketHandler("user1"),
                "/ws/user2", new ChatWebSocketHandler("user2")
        );
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(urlMap);
        mapping.setOrder(-1); // must be higher priority than DispatcherHandler
        return mapping;
    }

    // Without this bean Spring WebFlux cannot upgrade HTTP → WebSocket
    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}