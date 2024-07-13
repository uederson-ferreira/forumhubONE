package br.com.alura.forumhub.controller.dto.resposta;

import br.com.alura.forumhub.dominio.entities.Topico;

import java.time.Instant;

public record RespostaItemDTO(
        String mensagem,
        Instant dataCriacao,
        Instant dataAlteracao,
        String topico,
        String usuario,
        Boolean status,
        Boolean solucao

) {
}
