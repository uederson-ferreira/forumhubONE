package br.com.alura.forumhub.dominio.repository;

import br.com.alura.forumhub.dominio.entities.Curso;
import br.com.alura.forumhub.dominio.entities.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RespostaRepository extends JpaRepository<Resposta, Long> {

}