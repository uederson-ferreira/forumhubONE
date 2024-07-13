package br.com.alura.forumhub.dominio.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "cursoId")
@Table(name = "tb_cursos")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curso_id")
    private Long cursoId;

    @NotBlank
    private String nome;

    @NotBlank
    private String categoria;

    private Boolean status = true;

    @CreationTimestamp
    private Instant dataCriacao;

    @UpdateTimestamp
    private Instant dataAlteracao;

    @OneToMany(mappedBy = "curso")
    @JsonManagedReference
    private List<Topico> topicos;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}


