CREATE TABLE resposta (
    resposta_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mensagem TEXT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    autor_id BIGINT NOT NULL,
    topico_id BIGINT NOT NULL,
    solucao BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (autor_id) REFERENCES usuario(id),
    FOREIGN KEY (topico_id) REFERENCES topico(id)
);