package br.com.alura.forumhub.dominio.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "topicoId")
@Table(name = "tb_topicos")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicoId;

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotBlank(message = "Mensagem é obrigatório")
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
    @JoinColumn(name = "curso_id")
    @JsonBackReference
    @JsonIgnore
    private Curso curso;

    @OneToMany(mappedBy = "topico")
    private List<Resposta> respostas;
}

