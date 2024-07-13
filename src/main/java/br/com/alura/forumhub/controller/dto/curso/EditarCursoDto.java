package br.com.alura.forumhub.controller.dto.curso;

public record EditarCursoDto(
        String nome,
        String categoria,
        Boolean status
) {
}
