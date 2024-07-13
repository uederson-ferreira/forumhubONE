package br.com.alura.forumhub.controller.dto.curso;

import java.time.Instant;

public record CursoItemDto(
        String nome,
        String categoria,
        Instant dataCriacao,
        Instant dataAlteracao,
        Boolean status,
        String usuario

) {
}
