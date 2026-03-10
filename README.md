# Order Microservices

Sistema de gerenciamento de pedidos composto por dois microserviços: um **Gateway API** responsável pela autenticação e roteamento, e um **Order API** que encapsula a lógica de negócio de pedidos.

## Sumário

1. [Tecnologias](#tecnologias)
2. [Arquitetura](#arquitetura)
3. [Pré-requisitos](#pré-requisitos)
4. [Como executar](#como-executar)
5. [Endpoints](#endpoints)
6. [Autenticação](#autenticação)
7. [Swagger](#swagger)
8. [Testes](#testes)
9. [Decisões arquiteturais](#decisões-arquiteturais)

## Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.5.11 | Framework base |
| Spring Security | 6.x | Autenticação e autorização |
| JJWT | 0.11.5 | Geração e validação de tokens JWT |
| H2 Database | 2.x | Banco de dados em memória |
| SpringDoc OpenAPI | 2.x | Documentação Swagger |
| Docker | 26.x | Containerização |
| Maven | 3.x | Build e gerenciamento de dependências |

## Arquitetura

```
                       ┌──────────────────────┐
                       │      Gateway API      │
  Cliente  ──────────► │   (porta 8080)        │
                       │                       │
                       │  - /auth/login         │
                       │  - /api/orders/**      │──────────►  Order API
                       │  - /swagger-ui         │             (interna)
                       │                       │
                       │  JWT Filter            │
                       │  SecurityConfig        │
                       └──────────────────────┘
```

O Gateway API é o único ponto de entrada. O Order API não possui porta exposta externamente e só é acessível pela rede interna do Docker.

## Pré-requisitos

- Java 21
- Maven 3.x
- Docker e Docker Compose (para execução via container)

## Como executar

### Via Docker (recomendado)

```bash
sudo docker compose up --build
```

O Gateway API ficará disponível em `http://localhost:8080`.

### Via terminal (desenvolvimento local)

Abra dois terminais na raiz do projeto:

**Terminal 1 (Order API na porta 8080):**
```bash
cd order-api
./mvnw spring-boot:run
```

**Terminal 2 (Gateway API na porta 8081):**
```bash
cd gateway-api
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

> Para execução local, ajuste a propriedade `order-api.base-url` no arquivo `gateway-api/src/main/resources/application.properties` para `http://localhost:8080`.

### Usuário padrão

Ao iniciar, o Gateway API cria automaticamente um usuário de teste:

| Campo | Valor |
|---|---|
| username | `usuario` |
| password | `senha123` |
| role | `USER` |

## Endpoints

### Autenticação

| Método | Caminho | Descrição | Acesso |
|---|---|---|---|
| POST | `/auth/login` | Autenticar e obter token JWT | Público |

**Request body:**
```json
{
  "username": "usuario",
  "password": "senha123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### Pedidos

Todos os endpoints de pedidos exigem autenticação via header `Authorization: Bearer <token>`.

| Método | Caminho | Descrição | Roles |
|---|---|---|---|
| POST | `/api/orders` | Criar pedido | USER, ADMIN |
| GET | `/api/orders/{id}` | Buscar pedido por ID | USER, ADMIN |
| PUT | `/api/orders/{id}` | Atualizar pedido | USER, ADMIN |
| DELETE | `/api/orders/{id}` | Remover pedido | ADMIN |
| POST | `/api/orders/{id}/items` | Adicionar item a um pedido | USER, ADMIN |

**Criar pedido (POST /api/orders):**
```json
{
  "customerName": "João Silva",
  "customerEmail": "joao@email.com",
  "items": [
    {
      "productName": "Teclado Mecânico",
      "quantity": 1,
      "unitPrice": 350.00
    }
  ]
}
```

**Adicionar item (POST /api/orders/{id}/items):**
```json
{
  "productName": "Mouse Sem Fio",
  "quantity": 2,
  "unitPrice": 120.00
}
```

## Autenticação

O sistema utiliza JWT (JSON Web Token) com assinatura HMAC SHA-256. O fluxo de autenticação:

1. O cliente envia credenciais para `POST /auth/login`
2. O Gateway valida as credenciais e retorna um token JWT
3. O cliente inclui o token no header `Authorization: Bearer <token>` nas requisições seguintes
4. O `JwtAuthenticationFilter` intercepta cada requisição, valida o token e configura o contexto de segurança
5. O `SecurityConfig` verifica se o usuário possui a role necessária para o endpoint

## Swagger

A documentação interativa está disponível em:

```
http://localhost:8080/swagger-ui.html
```

O Swagger UI exibe todos os endpoints (autenticação e pedidos) em uma única visualização. Para testar endpoints autenticados:

1. Execute `POST /auth/login` com as credenciais do usuário padrão
2. Copie o valor do campo `accessToken` da resposta
3. Clique no botão "Authorize" no topo da página
4. Cole o token no campo e confirme
5. Todas as requisições seguintes incluirão o token automaticamente

## Testes

### Executar testes do Order API

```bash
cd order-api
./mvnw test
```

Cobertura: `OrderServiceTest` (7 testes), `OrderMapperTest` (8 testes), `OrderControllerIntegrationTest` (6 testes).

### Executar testes do Gateway API

```bash
cd gateway-api
./mvnw test
```

Cobertura: `JwtServiceTest` (5 testes), `AuthServiceTest` (2 testes), `AuthControllerIntegrationTest` (4 testes).

Total: **34 testes** (22 order-api + 12 gateway-api).

## Decisões arquiteturais

### Gateway como ponto único de entrada (Requisitado)

Todo o tráfego externo passa pelo Gateway API. O Order API não possui porta exposta no Docker e só é acessível pela rede interna. Isso centraliza a autenticação em um único lugar e permite adicionar novos microserviços sem expor cada um individualmente.

### JWT stateless

A escolha por JWT com sessão `STATELESS` elimina a necessidade de armazenar sessões no servidor. Cada requisição carrega suas próprias credenciais no token, o que simplifica a escalabilidade horizontal, pois qualquer instância do Gateway pode validar o token independentemente.

### Proxy com DTOs tipados no Gateway

O Gateway possui cópias dos DTOs do Order API (`OrderRequestDTO`, `OrderResponseDTO`, `OrderItemDTO`). Essa duplicação é intencional: permite que o Swagger UI exiba os schemas de entrada e saída diretamente na documentação unificada, sem depender de spec proxying ou JSON manipulation. A troca é a necessidade de manter os DTOs sincronizados entre os dois serviços.

### RestClient para comunicação entre serviços

O `RestClient` do Spring 6 foi escolhido por ser a API moderna e fluente para chamadas HTTP síncronas. Diferente do `RestTemplate` (legado) ou do `WebClient` (reativo), o RestClient oferece uma sintaxe limpa sem adicionar a complexidade de programação reativa que não é necessária neste contexto.

### H2 em memória

O H2 foi utilizado como banco de dados em memória para simplificar a execução do projeto sem necessidade de infraestrutura externa. Em produção, seria substituído por um banco relacional como PostgreSQL.

### Tratamento global de exceções

Ambos os serviços utilizam `@ControllerAdvice` para centralizar o tratamento de erros, garantindo respostas padronizadas com `timestamp`, `status`, `error` e `message`. No Gateway, o handler trata especificamente erros de autenticação (401), validação (400), indisponibilidade do Order API (503) e propagação de erros HTTP do serviço downstream.

### Estrutura de pacotes por domínio

Os pacotes seguem uma organização por domínio funcional (`auth`, `orders`) com separação em camadas internas (`api`, `service`, `domain`, `infrastructure`). Isso facilita a navegação e mantém a coesão, permitindo que cada domínio evolua independentemente.

### Domínio rico (Rich Domain Model)

A lógica de negócio está concentrada nas entidades de domínio, não na camada de serviço. A entidade `Order` é responsável por adicionar itens (`addItem`), associar a referência bidirecional com `OrderItem` e recalcular o valor total do pedido (`calculateTotalAmount`) a cada modificação. A entidade `OrderItem` calcula seu próprio subtotal (`calculateSubTotal`) a partir da quantidade e do preço unitário. Essa abordagem evita um serviço "anêmico" e garante que as regras de cálculo sejam sempre aplicadas, independentemente de qual camada altera o estado da entidade.
