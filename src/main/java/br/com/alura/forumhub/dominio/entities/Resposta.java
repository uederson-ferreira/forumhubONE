package br.com.alura.forumhub.dominio.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "respostaId")
@Table(name = "tb_respostas")
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long respostaId;

    @NotBlank
    @Column(columnDefinition = "TEXT CHARACTER SET utf8")
    private String mensagem;

    @CreationTimestamp
    private Instant dataCriacao;

    @UpdateTimestamp
    private Instant dataAlteracao;

    private Boolean status = true;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @ManyToOne
    @JoinColumn(name = "topico_id")
    private Topico topico;

    private Boolean solucao = false;
}

