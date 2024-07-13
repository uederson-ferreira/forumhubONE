package br.com.alura.forumhub.controller;

import br.com.alura.forumhub.controller.dto.resposta.*;
import br.com.alura.forumhub.dominio.entities.Perfil;
import br.com.alura.forumhub.dominio.entities.Resposta;
import br.com.alura.forumhub.dominio.repository.RespostaRepository;
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
@RequestMapping("/respostas")
@SecurityRequirement(name = "bearer-key")
public class RespostaController {

    @Autowired
    private final RespostaRepository respostaRepository;
    @Autowired
    private final TopicoRepository topicoRepository;
    @Autowired
    private final UsuarioRepository usuarioRepository;

    public RespostaController(RespostaRepository respostaRepository, TopicoRepository topicoRepository, UsuarioRepository usuarioRepository) {
        this.respostaRepository = respostaRepository;
        this.topicoRepository = topicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<RespostaDTO> listarRespostas(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        var repostas = respostaRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "dataAlteracao")).map(resposta -> {
            String tituloTopico = resposta.getTopico() != null ? resposta.getTopico().getTitulo() : "Tópico não especificado";
            assert resposta.getTopico() != null;
            return new RespostaItemDTO(resposta.getMensagem(), resposta.getDataCriacao(), resposta.getDataAlteracao(), resposta.getTopico().getTitulo(), resposta.getAutor().getNome(), resposta.getStatus(), resposta.getSolucao());
        });

        return ResponseEntity.ok(new RespostaDTO(repostas.getContent(), page, pageSize, repostas.getTotalPages(), repostas.getTotalElements()));
    }

    // Detalhamento de tópicos
    @GetMapping("/{id}")
    public ResponseEntity<RespostaItemDTO> listarRespostaPorId(@PathVariable Long id) {
        Optional<Resposta> optionalResposta = respostaRepository.findById(id);
        if (optionalResposta.isPresent()) {
            Resposta reposta = optionalResposta.get();
            String tituloTopico = reposta.getTopico() != null ? reposta.getTopico().getTitulo() : "Tópico não especificado";
            RespostaItemDTO respostaDto = new RespostaItemDTO(reposta.getMensagem(), reposta.getDataCriacao(), reposta.getDataAlteracao(), reposta.getTopico().getTitulo(), reposta.getAutor().getNome(), reposta.getStatus(), reposta.getSolucao());
            return ResponseEntity.ok(respostaDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity criarResposta(@RequestBody @Valid CriarRespostaDTO dto, JwtAuthenticationToken token, UriComponentsBuilder uriBuilder) {
        // Busque o usuário pelo token
        var usuario = usuarioRepository.findById(UUID.fromString(token.getName()));
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Busque o tópico pelo ID fornecido no DTO
        var topico = topicoRepository.findById(Long.valueOf(dto.topicoId()));
        if (topico.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tópico não encontrado");
        }

        // Crie a nova reposta
        var resposta = new Resposta();
        resposta.setMensagem(dto.mensagem());
        resposta.setTopico(topico.get()); // Associe o tópico a resposta
        resposta.setAutor(usuario.get()); // Associe o autor a resposta

        // Salve o tópico no repositório
        respostaRepository.save(resposta);

        // Crie a URI de resposta
        var uri = uriBuilder.path("/respostas/{id}").buildAndExpand(resposta.getRespostaId()).toUri();
        return ResponseEntity.created(uri).body(new DetalharRespostaDTO(resposta));
    }

    @PutMapping("/{id}")
    public ResponseEntity editarResposta(@PathVariable("id") Long respostaId, @RequestBody @Valid EditarRespostaDTO dto, JwtAuthenticationToken token, UriComponentsBuilder uriBuilder) {
        // Busque o usuário pelo token
        var usuario = usuarioRepository.findById(UUID.fromString(token.getName()));
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Busque o tópico pelo ID
        var resposta = respostaRepository.findById(respostaId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Verifique se o usuário é administrador ou o autor do tópico
        var isAdmin = usuario.get().getPerfis().stream().anyMatch(perfil -> perfil.getNome().equalsIgnoreCase(Perfil.Values.ADMIN.name()));
        if (isAdmin || resposta.getAutor().getUsuarioId().equals(UUID.fromString(token.getName()))) {

            // Atualize o tópico com as novas informações
            resposta.setMensagem(dto.mensagem());

            // Atualize o status do tópico se necessário
            if (dto.status() != null) {
                resposta.setStatus(dto.status());
            }

            // Atualize a s do tópico se necessário
            if (dto.solucao() != null) {
                resposta.setSolucao(dto.solucao());
            }

            // Salve o tópico no repositório
            respostaRepository.save(resposta);

            // Crie a URI de resposta
            var uri = uriBuilder.path("/respostas/{id}").buildAndExpand(resposta.getRespostaId()).toUri();
            return ResponseEntity.created(uri).body(new DetalharRespostaDTO(resposta));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirReposta(@PathVariable("id") Long respostaId, JwtAuthenticationToken token) {
        // Recupera o usuário pelo ID extraído do token
        var usuario = usuarioRepository.findById(UUID.fromString(token.getName()));

        // Verifica se o usuário existe
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Recupera o tópico pelo ID fornecido, ou lança uma exceção se não encontrado
        var resposta = respostaRepository.findById(respostaId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Verifica se o usuário tem perfil de administrador
        var isAdmin = usuario.get().getPerfis().stream().anyMatch(perfil -> perfil.getNome().equalsIgnoreCase(Perfil.Values.ADMIN.name()));

        // Verifica se o usuário é administrador ou autor do tópico
        if (isAdmin || resposta.getAutor().getUsuarioId().equals(UUID.fromString(token.getName()))) {
            // Exclui o resposta
            respostaRepository.deleteById(respostaId);
            return ResponseEntity.noContent().build();
        } else {
            // Retorna status 403 Forbidden se o usuário não tiver permissão para excluir o tópico
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
