package com.example.chatreactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatService {

    // Multicast hot sink — never completes, fans out to all active subscribers
    private static final Sinks.Many<Message> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    // In-memory history so reconnecting users see past messages
    private static final List<Message> history = new CopyOnWriteArrayList<>();

    public static void storeMessage(String sender, String receiver, String content) {
        Message msg = new Message(sender, receiver, content, LocalDateTime.now().toString());
        history.add(msg);
        // EMIT_FAILURE is safe to ignore here — means no subscribers yet
        sink.tryEmitNext(msg);
    }

    /**
     * Returns past messages for this recipient, then tails the live sink.
     * The live sink never completes, so this Flux stays open indefinitely
     * — exactly what we need to keep the WebSocket outbound alive.
     */
    public static Flux<Message> getMessagesFor(String recipient) {
        Flux<Message> live = sink.asFlux()
                .filter(m -> m.getReceiver().equals(recipient));

        List<Message> past = history.stream()
                .filter(m -> m.getReceiver().equals(recipient))
                .toList();

        // concatWith: emits past first (finite), then switches to live (infinite)
        return Flux.fromIterable(past).concatWith(live);
    }

    // ── Message ──────────────────────────────────────────────────────────────

    public static class Message {
        private final String sender;
        private final String receiver;
        private final String message;
        private final String timestamp;

        public Message(String sender, String receiver, String message, String timestamp) {
            this.sender    = sender;
            this.receiver  = receiver;
            this.message   = message;
            this.timestamp = timestamp;
        }

        public String getSender()    { return sender; }
        public String getReceiver()  { return receiver; }
        public String getMessage()   { return message; }
        public String getTimestamp() { return timestamp; }

        public String toJson() {
            return "{"
                    + "\"sender\":\""    + esc(sender)    + "\","
                    + "\"receiver\":\""  + esc(receiver)  + "\","
                    + "\"message\":\""   + esc(message)   + "\","
                    + "\"timestamp\":\"" + esc(timestamp) + "\""
                    + "}";
        }

        private static String esc(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}