package br.com.alura.forumhub.dominio.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "perfilId")
@Table(name = "tb_perfis")

public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long perfilId;

    private String nome;

    public enum Values {

        ADMIN(1L),
        USUARIO(2L);

        Long perfilId;

        Values(Long perfilId) {
            this.perfilId = perfilId;
        }

        public Long getPerfilId() {
            return perfilId;
        }
    }
}