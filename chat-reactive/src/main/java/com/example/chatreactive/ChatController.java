package com.example.chatreactive;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    private static final ConcurrentHashMap<String, String> messageStore = new ConcurrentHashMap<>();
    private static final Object lock = new Object();

    @GetMapping("/user1")
    public String getUser1Page(Model model) {
        // Get messages for user1 (from user2's perspective)
        model.addAttribute("messages", getMessagesForUser("user2"));
        return "user1";
    }

    @GetMapping("/user2")
    public String getUser2Page(Model model) {
        // Get messages for user2 (from user1's perspective)
        model.addAttribute("messages", getMessagesForUser("user1"));
        return "user2";
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestParam String sender,
                              @RequestParam String receiver,
                              @RequestParam String message) {
        // Store the message
        synchronized (lock) {
            long timestamp = System.currentTimeMillis();
            String key = sender + "_" + receiver + "_" + timestamp;
            messageStore.put(key, message);
        }

        return "redirect:/";
    }

    private Flux<String> getMessagesForUser(String user) {
        // Filter messages based on the user
        return Flux.fromIterable(messageStore.keySet())
                .filter(key -> key.contains(user))
                .map(key -> messageStore.get(key));
    }
}
