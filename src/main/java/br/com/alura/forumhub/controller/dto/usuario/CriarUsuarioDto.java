package br.com.alura.forumhub.controller.dto.usuario;

public record CriarUsuarioDto(
        String nome,
        String email,
        String login,
        String senha
) {
}
