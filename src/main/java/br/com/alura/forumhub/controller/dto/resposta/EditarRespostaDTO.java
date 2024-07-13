package br.com.alura.forumhub.controller.dto.resposta;

public record EditarRespostaDTO(
        String mensagem,
        Boolean status,
        Boolean solucao
) {
}
