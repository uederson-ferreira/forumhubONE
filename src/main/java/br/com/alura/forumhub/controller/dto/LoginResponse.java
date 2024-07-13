package br.com.alura.forumhub.controller.dto;

public record LoginResponse(
        String accessToken,
        Long expiresIn) {
}