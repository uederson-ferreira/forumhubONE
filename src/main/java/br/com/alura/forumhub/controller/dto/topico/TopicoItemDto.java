package br.com.alura.forumhub.controller.dto.topico;

import br.com.alura.forumhub.dominio.entities.Resposta;
import br.com.alura.forumhub.dominio.entities.Topico;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record TopicoItemDto(
        String titulo,
        String mensagem,
        Instant dataCriacao,
        Boolean status,
        String autor,
        String curso
) {
}
