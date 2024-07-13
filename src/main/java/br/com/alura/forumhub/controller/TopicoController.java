package br.com.alura.forumhub.controller;

import br.com.alura.forumhub.controller.dto.topico.*;
import br.com.alura.forumhub.dominio.entities.Perfil;
import br.com.alura.forumhub.dominio.entities.Topico;
import br.com.alura.forumhub.dominio.repository.CursoRepository;
import br.com.alura.forumhub.dominio.repository.TopicoRepository;
import br.com.alura.forumhub.dominio.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final TopicoRepository topicoRepository;
    @Autowired
    private final CursoRepository cursoRepository;

    public TopicoController(TopicoRepository topicoRepository, UsuarioRepository usuarioRepository, CursoRepository cursoRepository) {
        this.topicoRepository = topicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
    }

    @GetMapping
    public ResponseEntity<TopicoDTO> listarTopicos(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        var topicos = topicoRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "dataAlteracao"))
                .map(topico -> {
                    assert topico.getCurso() != null;
                    return new TopicoItemDto(
                            topico.getTitulo(),
                            topico.getMensagem(),
                            topico.getDataCriacao(),
                            topico.getStatus(),
                            topico.getAutor().getNome(),
                            topico.getCurso().getNome()
                    );
                });

        return ResponseEntity.ok(new TopicoDTO(
                topicos.getContent(),
                page,
                pageSize,
                topicos.getTotalPages(),
                topicos.getTotalElements()));
    }

    // Detalhamento de tópicos
    @GetMapping("/{id}")
    public ResponseEntity<TopicoItemDto> listarTopicoPorId(@PathVariable Long id) {
        Optional<Topico> optionalTopico = topicoRepository.findById(id);
        if (optionalTopico.isPresent()) {
            Topico topico = optionalTopico.get();
            TopicoItemDto topicoDto = new TopicoItemDto(
                    topico.getTitulo(),
                    topico.getMensagem(),
                    topico.getDataCriacao(),
                    topico.getStatus(),
                    topico.getAutor().getNome(),
                    topico.getCurso().getNome()
            );
            return ResponseEntity.ok(topicoDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity criarTopico(@RequestBody @Valid CriarTopicoDto dto, JwtAuthenticationToken token, UriComponentsBuilder uriBuilder) {
        // Busque o usuário pelo token
        var usuario = usuarioRepository.findById(UUID.fromString(token.getName()));
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Verifique se já existe um tópico com o mesmo título e mensagem
        if (topicoRepository.existsByTituloAndMensagem(dto.titulo(), dto.mensagem())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um tópico com o mesmo título e mensagem!");
        }

        // Busque o curso pelo ID fornecido no DTO
        var curso = cursoRepository.findById(Long.valueOf(dto.cursoId()));
        if (curso.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Curso não encontrado");
        }

        // Crie o novo tópico
        var topico = new Topico();
        topico.setAutor(usuario.get());
        topico.setTitulo(dto.titulo());
        topico.setMensagem(dto.mensagem());
        topico.setCurso(curso.get());  // Associe o curso ao tópico

        // Salve o tópico no repositório
        topicoRepository.save(topico);

        // Crie a URI de resposta
        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getTopicoId()).toUri();
        return ResponseEntity.created(uri).body(new DetalharTopicoDTO(topico));
    }

    @PutMapping("/{id}")
    public ResponseEntity editarTopico(@PathVariable("id") Long topicoId, @RequestBody @Valid EditarTopicoDto dto, JwtAuthenticationToken token, UriComponentsBuilder uriBuilder) {
        // Busque o usuário pelo token
        var usuario = usuarioRepository.findById(UUID.fromString(token.getName()));
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Busque o tópico pelo ID
        var topico = topicoRepository.findById(topicoId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Verifique se o usuário é administrador ou o autor do tópico
        var isAdmin = usuario.get().getPerfis().stream().anyMatch(perfil -> perfil.getNome().equalsIgnoreCase(Perfil.Values.ADMIN.name()));
        if (isAdmin || topico.getAutor().getUsuarioId().equals(UUID.fromString(token.getName()))) {
            // Busque o curso pelo ID fornecido no DTO
            var curso = cursoRepository.findById(Long.valueOf(dto.cursoId()));
            if (curso.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Curso não encontrado");
            }

            // Atualize o tópico com as novas informações
            topico.setTitulo(dto.titulo());
            topico.setMensagem(dto.mensagem());
            topico.setCurso(curso.get());  // Associe o curso ao tópico

            // Atualize o status do tópico se necessário
            if (dto.status() != null) {
                topico.setStatus(dto.status());
            }

            // Salve o tópico no repositório
            topicoRepository.save(topico);

            // Crie a URI de resposta
            var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getTopicoId()).toUri();
            return ResponseEntity.created(uri).body(new DetalharTopicoDTO(topico));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTopico(@PathVariable("id") Long topicoId, JwtAuthenticationToken token) {
        // Recupera o usuário pelo ID extraído do token
        var usuario = usuarioRepository.findById(UUID.fromString(token.getName()));

        // Verifica se o usuário existe
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Recupera o tópico pelo ID fornecido, ou lança uma exceção se não encontrado
        var topico = topicoRepository.findById(topicoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Verifica se o usuário tem perfil de administrador
        var isAdmin = usuario.get().getPerfis().stream()
                .anyMatch(perfil -> perfil.getNome().equalsIgnoreCase(Perfil.Values.ADMIN.name()));

        // Verifica se o usuário é administrador ou autor do tópico
        if (isAdmin || topico.getAutor().getUsuarioId().equals(UUID.fromString(token.getName()))) {
            // Exclui o tópico
            topicoRepository.deleteById(topicoId);
            return ResponseEntity.noContent().build();
        } else {
            // Retorna status 403 Forbidden se o usuário não tiver permissão para excluir o tópico
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
