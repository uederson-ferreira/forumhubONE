package br.com.alura.forumhub.controller.dto.resposta;

public record CriarRespostaDTO(
        String mensagem,
        Integer topicoId
) {
}
