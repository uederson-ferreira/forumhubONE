package br.com.alura.forumhub.controller.dto.topico;


import br.com.alura.forumhub.dominio.entities.Resposta;
import br.com.alura.forumhub.dominio.entities.Topico;

import java.time.Instant;
import java.util.List;

public record DetalharTopicoDTO(
        String titulo,
        String mensagem,
        Instant dataCriacao,
        String status,
        String autor,
        String curso,
        List<Resposta> respostas

) {

    public DetalharTopicoDTO(Topico topico) {
        this(
                topico.getTitulo(),
                topico.getMensagem(),
                topico.getDataCriacao(),
                topico.getStatus() != null ? topico.getStatus().toString() : "Status não especificado",
                topico.getAutor().getNome(),
                topico.getCurso().getNome(),
                topico.getRespostas()
        );
    }
}


