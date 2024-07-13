package br.com.alura.forumhub.controller.dto.resposta;

import br.com.alura.forumhub.controller.dto.topico.TopicoItemDto;

import java.util.List;

public record RespostaDTO(
        List<RespostaItemDTO> itensDaResposta,
        int pagina,
        int tamanhoDaPagina,
        int totalDePaginas,
        long totalDeElementos) {
}
