package com.ginomarin.persistencia.controllers;


import com.ginomarin.persistencia.dto.ChatRequestDTO;
import com.ginomarin.persistencia.dto.ChatResponseDTO;
import com.ginomarin.persistencia.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PreAuthorize("hasAuthority('chat:usar')")
    @PostMapping
    public ChatResponseDTO chat(@RequestBody ChatRequestDTO request){
        return chatService.chat(request);
    }


}
