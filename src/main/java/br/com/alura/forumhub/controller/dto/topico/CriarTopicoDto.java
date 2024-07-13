package br.com.alura.forumhub.controller.dto.topico;

public record CriarTopicoDto(
        String titulo,
        String mensagem,
        Integer cursoId
) {
}
