# Fórum Hub API

## Descrição

A ForumHub API é uma aplicação desenvolvida em Spring Boot que oferece funcionalidades de gerenciamento de tópicos em um fórum online. Os usuários podem criar, visualizar, atualizar e excluir tópicos, além de autenticar-se para acessar as funcionalidades protegidas da API.

Projeto desenlvido para o curso: "Praticando Spring Framework: Challenge Fórum Hub" - Back End Turma 06 do ONE - Oracle Next Education & Alura

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3
- Maven
- MySQL
- Flyway Migration
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- JSON Web Token (JWT)
- SpringFox Swagger

## Pré-requisitos

- JDK 17 instalado
- IDE Java (como NetBeans, Eclipse ou IntelliJ IDEA)

## Gerar a chave pública e privada do token JWT

1. No terminal, digitar o seguinte comando na pasta raiz do projeto:
   ```
   cd src\main\resources
   ```
2. No terminal, dentro da pasta "src\main\resources" digitar o seguinte comando para gerar a chave privada:
   ```
   opensssl genrsa > app.key
   ```
3. No terminal, dentro da pasta "src\main\resources" digitar o seguinte comando para gerar a chave pública:
   ```
   openssl rsa -in app.key -pubout -out app.pub
   ```

## Endpoints Principais

- `/topicos`: Endpoint para operações CRUD de tópicos.
- `/login`: Endpoint para autenticar usuários e obter token JWT.
- `/swagger-ui.html`: Interface gráfica do Swagger para visualizar e testar os endpoints da API.

## Como Executar

1. Clone este repositório:
   ```
   git clone https://github.com/uederson-ferreira/forumhubONE.git
   ```
2. Importe o projeto em sua IDE favorita (ex: IntelliJ IDEA, Eclipse, NetBeans).
3. Configure o banco de dados MySQL e atualize as informações de conexão no arquivo `application.properties`.
4. Execute a aplicação Spring Boot.
5. Acesse a interface gráfica do Swagger em `http://localhost:8080/swagger-ui.html` para visualizar e testar os endpoints da API.

## Contato

Para mais informações, entre em contato através do email: uedersonferreira@gmail.com

**Link do Projeto:** [https://github.com/uederson-ferreira/forumhubONE](https://github.com/uederson-ferreira/forumhub)
<br>
**Meu Perfil na Alura:** [https://cursos.alura.com.br/user/uedersonferreira](https://cursos.alura.com.br/user/uedersonferreira)

<br>
Developed by Uederson Ferreira
