package com.ginomarin.persistencia.dto;

public record ChatRequestDTO(
        String conversationId,
        String message
) {
}
