package br.com.alura.forumhub.controller.dto;

public record LoginRequest(
        String login,
        String senha
) {
}