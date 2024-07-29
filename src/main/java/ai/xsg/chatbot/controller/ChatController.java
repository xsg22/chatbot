package ai.xsg.chatbot.controller;

import ai.xsg.chatbot.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    ChatService chatService = new ChatService();

    @PostMapping("/block")
    public byte[] chatBlock(@RequestParam("audio") MultipartFile file) throws IOException {
        byte[] voiceByte = file.getBytes();
        return chatService.send(voiceByte);
    }
}
