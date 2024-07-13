package br.com.alura.forumhub.controller.dto.topico;

public record EditarTopicoDto(
        String titulo,
        String mensagem,
        Integer cursoId,
        Boolean status
) {
}
