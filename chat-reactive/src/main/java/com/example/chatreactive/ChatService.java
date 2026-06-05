package com.example.chatreactive;


import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ChatService {
    private static final ConcurrentHashMap<String, ChatService.Message> messageStore = new ConcurrentHashMap<>();
    private static final AtomicLong counter = new AtomicLong(0L);

    public static void storeMessage(String message) {
        long id = counter.incrementAndGet();
        String timestamp = LocalDateTime.now().toString();
        ChatService.Message msg = new ChatService.Message(id, message, timestamp);
        messageStore.put("msg_" + id, msg);
    }

    public static Flux<String> getMessages() {
        return Flux.fromIterable(messageStore.values())
                .map(msg -> "{ \"id\": " + msg.getId() + ", \"message\": \"" + msg.getMessage() + "\", \"timestamp\": \"" + msg.getTimestamp() + "\" }")
                .cast(String.class);
    }

    public static class Message {
        private final long id;
        private final String message;
        private final String timestamp;

        public Message(long id, String message, String timestamp) {
            this.id = id;
            this.message = message;
            this.timestamp = timestamp;
        }

        public long getId() { return id; }
        public String getMessage() { return message; }
        public String getTimestamp() { return timestamp; }
    }
}





