package com.example.chatreactive;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/user1")
    public String user1() {
        return "user1";
    }

    @GetMapping("/user2")
    public String user2() {
        return "user2";
    }
}