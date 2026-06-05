package com.example.chatreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ChatReactiveApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatReactiveApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> chatRoutes() {
        return RouterFunctions.route(
                RequestPredicates.POST("/chat/send"),
                request -> request.bodyToMono(String.class)
                        .doOnNext(message -> {
                            if (message == null || message.trim().isEmpty()) {
                                throw new IllegalArgumentException("Message cannot be empty.");
                            }
                            ChatService.storeMessage(message);
                        })
                        .then(ServerResponse.ok().build())
                        .onErrorResume(IllegalArgumentException.class, e ->
                                ServerResponse.status(HttpStatus.BAD_REQUEST)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()))
                        )
                        .onErrorResume(e ->
                                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send message: " + e.getMessage()))
                        )
        ).andRoute(
                RequestPredicates.GET("/chat/receive"),
                request -> {
                    try {
                        Flux<String> messages = ChatService.getMessages();
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(messages, String.class);
                    } catch (Exception e) {
                        ErrorResponse errorBody = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve messages: " + e.getMessage());
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(errorBody);
                    }
                }
        );
    }
}

