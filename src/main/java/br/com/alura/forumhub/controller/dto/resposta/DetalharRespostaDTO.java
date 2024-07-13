package br.com.alura.forumhub.controller.dto.resposta;


import br.com.alura.forumhub.dominio.entities.Curso;
import br.com.alura.forumhub.dominio.entities.Resposta;
import br.com.alura.forumhub.dominio.entities.Topico;

import java.time.Instant;
import java.util.List;

public record DetalharRespostaDTO(
        String mensagem,
        Instant dataCriacao,
        Instant dataAlteracao,
        String topico,
        String usuario,
        Boolean status,
        Boolean solucao
) {

    public DetalharRespostaDTO(Resposta resposta) {
        this(
                resposta.getMensagem(),
                resposta.getDataCriacao(),
                resposta.getDataAlteracao(),
                resposta.getTopico().getTitulo(),
                resposta.getAutor().getNome(),
                resposta.getStatus(),
                resposta.getSolucao()
        );
    }
}


