package br.com.alura.forumhub.dominio.repository;

import br.com.alura.forumhub.dominio.entities.Topico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicoRepository extends JpaRepository<Topico, Long> {

    @Override
    Optional<Topico> findById(Long id);

    boolean existsByTituloAndMensagem(String titulo, String mensagem);
}