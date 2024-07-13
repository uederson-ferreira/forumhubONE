package br.com.alura.forumhub.controller;

import br.com.alura.forumhub.controller.dto.curso.*;
import br.com.alura.forumhub.dominio.entities.Curso;
import br.com.alura.forumhub.dominio.entities.Perfil;
import br.com.alura.forumhub.dominio.repository.CursoRepository;
import br.com.alura.forumhub.dominio.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/cursos")
@SecurityRequirement(name = "bearer-key")
public class CursoController {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    public CursoController(CursoRepository cursoRepository, UsuarioRepository usuarioRepository) {
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<CursoDto> listarCursos(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        var topicos = cursoRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "dataAlteracao"))
                .map(curso -> {
                    return new CursoItemDto(
                            curso.getNome(),
                            curso.getCategoria(),
                            curso.getDataCriacao(),
                            curso.getDataAlteracao(),
                            curso.getStatus(),
                            curso.getUsuario().getNome()
                    );
                });

        return ResponseEntity.ok(new CursoDto(
                topicos.getContent(),
                page,
                pageSize,
                topicos.getTotalPages(),
                topicos.getTotalElements()));
    }

    @PostMapping
    public ResponseEntity criarCurso(@RequestBody @Valid EditarCursoDto dto, JwtAuthenticationToken token, UriComponentsBuilder uriBuilder) {
        var usuario = usuarioRepository.findById(UUID.fromString(token.getName()));

        var curso = new Curso();
        curso.setUsuario(usuario.get());
        curso.setNome(dto.nome());
        curso.setCategoria(dto.categoria());

        // Atualize o status do tópico se necessário
        if (dto.status() != null) {
            curso.setStatus(dto.status());
        }

        cursoRepository.save(curso);

        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(curso.getCursoId()).toUri();
        return ResponseEntity.created(uri).body(new DetalharCursoDTO(curso));

    }

    @PutMapping("/{id}")
    public ResponseEntity editarCurso(@PathVariable("id") Long cursoId, @RequestBody @Valid CriarCursoDto dto, JwtAuthenticationToken token, UriComponentsBuilder uriBuilder) {
        var usuario = usuarioRepository.findById(UUID.fromString(token.getName()));
        var curso = cursoRepository.findById(cursoId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = usuario.get().getPerfis().stream().anyMatch(perfil -> perfil.getNome().equalsIgnoreCase(Perfil.Values.ADMIN.name()));

        if (isAdmin || curso.getUsuario().getUsuarioId().equals(UUID.fromString(token.getName()))) {
            curso.setNome(dto.nome());
            curso.setCategoria(dto.categoria());

            cursoRepository.save(curso);

            var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(curso.getCursoId()).toUri();
            return ResponseEntity.created(uri).body(new DetalharCursoDTO(curso));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCurso(@PathVariable("id") Long cursoId, JwtAuthenticationToken token) {
        var usuario = usuarioRepository.findById(UUID.fromString(token.getName()));
        var curso = cursoRepository.findById(cursoId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = usuario.get().getPerfis().stream().anyMatch(perfil -> perfil.getNome().equalsIgnoreCase(Perfil.Values.ADMIN.name()));

        if (isAdmin || curso.getUsuario().getUsuarioId().equals(UUID.fromString(token.getName()))) {
            cursoRepository.deleteById(cursoId);

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.noContent().build();
    }
}
