package br.com.alura.forumhub.controller.dto.curso;


import br.com.alura.forumhub.dominio.entities.Curso;
import br.com.alura.forumhub.dominio.entities.Topico;

import java.util.List;

public record DetalharCursoDTO(
        Long id,
        String nome,
        String categoria,
        List<Topico> topicos,
        String usuario
) {

    public DetalharCursoDTO(Curso curso) {
        this(
                curso.getCursoId(),
                curso.getNome(),
                curso.getCategoria(),
                curso.getTopicos(),
                curso.getUsuario().getNome()
        );
    }
}


