package br.com.alura.forumhub.controller;

import br.com.alura.forumhub.dominio.entities.Perfil;
import br.com.alura.forumhub.controller.dto.LoginRequest;
import br.com.alura.forumhub.controller.dto.LoginResponse;
import br.com.alura.forumhub.dominio.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class LoginController {

    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public LoginController(JwtEncoder jwtEncoder,
                           UsuarioRepository usuarioRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        var usuario = usuarioRepository.findByLogin(loginRequest.login());

        if (usuario.isEmpty() || !usuario.get().isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("usuário ou senha é inválido!");
        }

        var agora = Instant.now();
        var tempoDeExpiracao = 600L; // 600 segundos -> 10 minutos

        var scopes = usuario.get().getPerfis()
                .stream()
                .map(Perfil::getNome)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("forumhub")// quem está gerando o TOKEN
                .subject(usuario.get().getUsuarioId().toString())// USUÁRIO do TOKEN
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(tempoDeExpiracao))// Tempo De Expiracao do TOKEN
                .claim("scope", scopes)// scope do usuário
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse(jwtValue, tempoDeExpiracao));
    }
}