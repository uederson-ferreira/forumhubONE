package br.com.alura.forumhub.dominio.repository;

import br.com.alura.forumhub.dominio.entities.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    Perfil findByNome(String nome);
}
