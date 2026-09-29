package com.ginomarin.persistencia.service;

import com.ginomarin.persistencia.dto.ChatRequestDTO;
import com.ginomarin.persistencia.dto.ChatResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatService {

    private static final String SYSTEM_PROMPT = """
            Eres un asistente de Veterinaria que ayuda a buscar información de Veterinarios
            de perros o entre ellos.
            
            No puedes inventar data: Debes traer la información a partir de las herramientas que tienes disponibles.
            """;

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, DuennoService duennoService){

        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(duennoService)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }


    public ChatResponseDTO chat(ChatRequestDTO request){
        String conversationId = request.conversationId() == null || request.conversationId().isBlank()
                ? UUID.randomUUID().toString()
                : request.conversationId();

        String reply = chatClient.prompt()
                .user(request.message())
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
        return new ChatResponseDTO(conversationId, reply);
    }


}
