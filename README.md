██████╗ █████╗ ██╗██╗  ██╗███████╗██╗     
██╔══██╗██╔══██╗██║██║ ██╔╝██╔════╝██║     
██████╔╝███████║██║█████╔╝ █████╗  ██║     
██╔═══╝ ██╔══██║██║██╔═██╗ ██╔══╝  ██║     
██║     ██║  ██║██║██║  ██╗███████╗███████╗
╚═╝     ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚══════╝╚══════╝

📌 Descrição Geral

Esta API implementa integralmente o Desafio Caixa – Painel de Investimentos, atendendo a todos os requisitos do edital, incluindo:

Receber envelope JSON de simulação

Validar parâmetros com base no banco

Filtrar produto adequado

Calcular simulação conforme regra de negócio

Persistir simulações em SQLite

Retornar histórico completo

Calcular perfil de risco

Recomendar produtos conforme comportamento

Disponibilizar telemetria da API

Proteger rotas com JWT

Executar 100% dentro de containers Docker

🏛️ Arquitetura da Solução
flowchart LR
User --> Auth[POST /auth/login]
Auth -->|JWT| API[API Quarkus]
API --> Sim[Simulação Service]
API --> Perfil[Perfil de Risco Service]
API --> Rec[Recomendação]
API --> Tele[Telemetria]
API --> DB[(SQLite - banco.db)]

🐳 Como Executar via Docker
✔ 1. Clone o repositório
git clone https://github.com/rogeriodesaf/desafio-painel-investimentos-quarkus.git

✔ 2. Execute o Docker Compose
docker compose up --build

✔ 3. Acesse

Swagger: http://localhost:8080/q/swagger-ui

Health: http://localhost:8080/q/health

🔐 Autenticação JWT
Login
POST /auth/login

Corpo:
{
"usuario": "admin",
"senha": "123"
} 
Tem permissão para acessar todos os 'endpoints protegidos da API.'
ou 
{
"usuario": "caixa",
"senha": "123"
}
Tem permissão para acessar todos os 'endpoints protegidos da API', exceto os de telemetria.

O token deve ser usado no botão Authorize do Swagger:

Evidências do token sendo gerado e utilizado encontra-se mais abaixo na sessão de evidências.





📡 Endpoints da API

Investimentos
| Método | Rota                          | Descrição                               |
| ------ | ----------------------------- | --------------------------------------- |
| GET    | `/investimentos/{clienteId}`  | Histórico de investimentos do cliente   |
| POST   | `/simular-investimento`       | Simula investimento e persiste no banco |
| GET    | `/simulacoes`                 | Lista todo o histórico                  |
| GET    | `/simulacoes/por-produto-dia` | Agrupa simulações por produto e data    |


Perfil de Risco
| Método | Rota                        | Descrição                         |
| ------ | --------------------------- | --------------------------------- |
| GET    | `/perfil-risco/{clienteId}` | Calcula e retorna perfil de risco |


Recomendação
| Método | Rota                              | Descrição                              |
| ------ | --------------------------------- | -------------------------------------- |
| GET    | `/produtos-recomendados/{perfil}` | Recomenda produtos adequados ao perfil |


Telemetria
| Método | Rota          | Descrição                |
| ------ | ------------- | ------------------------ |
| GET    | `/telemetria` | Métricas internas da API |


Autenticação / Status
| Método | Rota          | Descrição     |
| ------ | ------------- | ------------- |
| POST   | `/auth/login` | Gera JWT      |
| GET    | `/status`     | Status da API |



🎯 Perfil de Risco – Lógica

O cálculo utiliza:

Volume investido
Frequência de movimentações
Preferência por liquidez
Histórico de comportamento

Faixas de Pontuação:
| Pontos | Perfil      |
| ------ | ----------- |
| 0–40   | Conservador |
| 41–70  | Moderado    |
| 71–100 | Agressivo   |



🧠 Motor de Recomendação

Para cada perfil, o sistema retorna produtos filtrados por:
Risco
Rentabilidade
Tipo do produto(CDB, LCI, Tesouro etc.)


💰 Simulação de Investimentos

A lógica utiliza:

Rentabilidade anual do produto
Conversão proporcional por mês
Cálculo acumulado final


💾 Banco de Dados SQLite

O arquivo do banco fica em:
sqlite/banco.db

O banco é carregado automaticamente pelo Docker.

🖼️ Evidências 

“Disponibilizar o código fonte, com todas as evidências no formato zip ou arquivo texto contendo link para o Git público.”

Todas as evidências foram capturadas e estão listadas abaixo:

### 🐳 Execução via Docker – Evidência

A aplicação é totalmente containerizada e sobe com apenas um comando:
docker compose up --build

Evidência 1 — Container Subindo:
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/0203b874b987dc6e1e9030dc56e9cfb18f949603/evidencias/docker_evidencia.png

Evidência 2 — Terminal:
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/refs/heads/master/evidencias/docker_terminal.png

### 📘 Swagger UI – Documentação da API Carregada

A API foi documentada automaticamente pelo Quarkus SmallRye OpenAPI e está disponível no endpoint:
http://localhost:8080/q/swagger-ui


O Swagger exibe todas as rotas do projeto, incluindo autenticação, simulação, histórico, telemetria, perfil de risco e recomendações.  
O carregamento da página confirma que o servidor está funcionando corretamente dentro do Docker.

**Evidências:**
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/refs/heads/master/evidencias/swagger.png
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/refs/heads/master/evidencias/swagger2.png
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/refs/heads/master/evidencias/swegger3.png


### 🔐 Token JWT – POST /auth/login

O endpoint de autenticação recebe usuário e senha, valida as credenciais e retorna um **token JWT** assinado com a chave privada configurada no projeto.

Esse token é utilizado para acessar endpoints protegidos através do botão **Authorize** no Swagger.

✔ Autenticação funcionando  
✔ JWT assinado corretamente  
✔ Segurança habilitada  
✔ Conformidade com o desafio

**Evidência:**

https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/bf70318725e761b661a67cf1b2e3e11daeaff219/evidencias/POST_auth_login%20funcionando.png


### 💰 Simulação de Investimento – POST /simular-investimento

O endpoint recebe `clienteId`, `valor`, `prazoMeses` e `tipoProduto`, valida o produto, calcula o rendimento conforme as regras cadastradas, registra a simulação no SQLite e retorna o resultado.

✔ Validação dos dados  
✔ Seleção do produto  
✔ Cálculo do rendimento  
✔ Persistência no banco  
✔ Telemetria registrada  
✔ Protegido por JWT

**Evidência da requisição:**
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/2ae386df993406ff870c88eabacf3bc8721a88eb/evidencias/simular_investimento_request.png

**Evidência da resposta:**
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/refs/heads/master/evidencias/simular_investimento.jpg


### 📜 Histórico de Simulações – GET /simulacoes

A evidência abaixo comprova o funcionamento da rota responsável por listar todo o histórico de simulações já persistidas no banco SQLite.  
Cada registro representa uma operação realizada anteriormente, contendo produto, valor investido, resultado e data.

✔ Persistência real no banco  
✔ Recuperação completa dos dados  
✔ Conformidade com o edital  
✔ Funcionamento consistente dentro do Docker

**Evidência:**

https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/8d5534c651f3a21919cbe067a57fc350f4cdcb13/evidencias/get_simulacoes.png


### 📊 Simulações Agrupadas por Produto e Data – GET /simulacoes/por-produto-dia

Este endpoint retorna uma visão consolidada das simulações realizadas, agrupadas por produto e por data.  
A resposta mostra quantas simulações ocorreram para cada produto em um determinado dia, bem como a média dos valores finais obtidos.

✔ Agrupamento por produto e data  
✔ Cálculo da média de valores simulados  
✔ Persistência consistente no banco SQLite  
✔ Cumprimento integral do requisito do edital

**Evidência:**

https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/f1162b19d4e0255c50a725667fefea64f8f964c6/evidencias/simulacoes_produto_dia.jpg

### 📡 Telemetria da Aplicação – GET /telemetria

A telemetria registra automaticamente o uso dos serviços da API, permitindo acompanhar quais endpoints estão sendo acionados e com qual frequência.  
Cada registro inclui o serviço monitorado, data da execução e a quantidade de chamadas.

✔ Observabilidade  
✔ Persistência no SQLite  
✔ Métricas por serviço  
✔ Conformidade com o fluxo esperado

**Evidência:**
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/e9a9e517ce16482660ba1b36baf74f0fca8fd917/evidencias/telemetria.jpg


### 📁 Investimentos do Cliente – GET /investimentos/{clienteId}

Este endpoint retorna todos os investimentos cadastrados para um cliente específico.  
A API valida o cliente, consulta o banco SQLite, monta a lista de investimentos e retorna os dados de forma organizada.

✔ Consulta ao banco  
✔ DTO padronizado  
✔ Dados completos do investimento  
✔ Protegido por JWT

**Evidência:**

https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/bb69105755f32a453d53383e955e2a5cb585f5eb/evidencias/investimentos-clienteid.png





### 🎯 Perfil de Risco – GET /perfil-risco/{clienteId}

Este endpoint calcula o perfil de risco de um cliente com base em suas informações financeiras.  
A API consulta o banco SQLite, aplica o algoritmo de pontuação e retorna o perfil classificado como *Conservador*, *Moderado* ou *Agressivo*.

✔ Consulta ao banco  
✔ Algoritmo de score  
✔ Classificação automática  
✔ Preparação para recomendação

**Evidência:**

Conservador:
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/778a238d15eb45a43ab21ff38a02a8902a32ead3/evidencias/perfil-risco-conservador.jpg

Moderado:
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/refs/heads/master/evidencias/perfil-risco-moderado.png

Agressivo:
https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/refs/heads/master/evidencias/perfil-risco-agressivo.png

### 🧠 Produtos Recomendados – GET /produtos-recomendados/{perfil}

Este endpoint retorna a lista de produtos adequados ao perfil de risco informado (*Conservador*, *Moderado* ou *Agressivo*).  
A API consulta o banco SQLite, filtra os produtos pelo nível de risco e retorna as opções compatíveis.

✔ Integração com o perfil de risco  
✔ Regras claras de recomendação  
✔ Dados vindos do SQLite  
✔ Protegido por JWT

**Evidência:**

https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/33af3a1e0be84d4ff1680b2d2c01382038530032/evidencias/produtos-recomendados-perfil.png

### ❗ Validação de Perfil Inválido – GET /produtos-recomendados/{perfil}

A API aceita apenas três perfis válidos: `conservador`, `moderado` e `agressivo`.

Quando qualquer outro valor é informado no endpoint, a API retorna:

- **HTTP 400 – Bad Request**
- **mensagem indicando que o perfil é inválido**

Esse comportamento demonstra robustez na validação dos parâmetros e conformidade com boas práticas REST.

**Evidência:**

https://raw.githubusercontent.com/rogeriodesaf/desafio-painel-investimentos-quarkus/4a6cd2d8fe2105e61a0528df6b702d1eff0069fe/evidencias/evidencia-400.png




🛠️ Tecnologias Utilizadas

Java 21

Quarkus 3.15

SQLite

Hibernate + Panache

JWT (smallrye-jwt)

Docker / Docker Compose

Swagger (OpenAPI 3)

RestAssured / JUnit 5

Mockito

👨‍💻 Autor

Desenvolvido por Rogério de Sá – Java Backend Developer

🐙 Link do Github para o projeto: https://github.com/rogeriodesaf/desafio-painel-investimentos-quarkus.git
📧 email: rogerio.figueiredo@caixa.gov.br
matricula Caixa: c157751-7