# BTG Orders Service — Desafio Técnico

[![Java 21](https://img.shields.io/badge/java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3](https://img.shields.io/badge/spring--boot-3.3-brightgreen)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/docker-ready-blue)](https://hub.docker.com/r/freshiagoomes/btg-orders-service)
[![Coverage](https://img.shields.io/badge/coverage-100%25-success)](./target/site/jacoco)
[![OpenAPI 3.0](https://img.shields.io/badge/spec-OpenAPI%203.0-blueviolet)](./openapi.yaml)

Microsserviço para **processamento de pedidos** com **APIs REST** e mensageria assíncrona via **RabbitMQ**. Construído com foco em **segurança**, **escalabilidade** e **boas práticas** adotadas em domínios bancários.

---

## Sumário

* [Visão Geral](#visão-geral)
* [Arquitetura (Clean + Delegate Pattern)](#arquitetura-clean--delegate-pattern)
* [Início Rápido (3 passos)](#início-rápido-3-passos)
* [Passo a passo para o uso do APP](#passo-a-passo-para-o-uso-do-app)
* [APIs REST (exemplos)](#apis-rest-exemplos)
* [Mensageria (formato de mensagem)](#mensageria-formato-de-mensagem)
* [Configuração (variáveis e perfis)](#configuração-variáveis-e-perfis)
* [Qualidade e Testes](#qualidade-e-testes)
* [Docker / Compose](#docker--compose)
* [Swagger UI](#swagger-ui)
* [Troubleshooting](#troubleshooting)
* [OpenAPI + Generator (Delegate Pattern)](#openapi--generator-delegate-pattern)

---

## Visão Geral

O serviço processa mensagens de pedidos recebidas via **RabbitMQ** e expõe endpoints para consulta de totais, contagem e listagem de pedidos por cliente. A especificação **OpenAPI** guia contratos e gera parte do boilerplate (Controller + Interfaces), permitindo um fluxo *contract-first* e mantendo documentação atualizada.

---

## Arquitetura (Clean + Delegate Pattern)

**Camadas:**

* **API (Gerada)**: contratos REST + delegação
* **Application**: orquestração e serviços de aplicação
* **Domain**: regras de negócio puras (use cases, entidades)
* **Infrastructure**: DB, MQ, configs e providers

**Fluxo:** API → Controller → Delegate → Resource → Service → UseCase → Provider → Database

```mermaid
flowchart LR
  A[OpenAPI Spec] -->|generate| B(Interface API)
  A -->|generate| C(Controller API)
  A -->|generate| D(Delegate API)
  C --> D
  D --> E[Resource (app.resource)]
  E --> F[Service (app.service)]
  F --> G[UseCase (domain.usecase)]
  G --> H[Provider (infra.*)]
  H --> I[(PostgreSQL)]
  H --> J[(RabbitMQ)]
```

---

## Início Rápido (3 passos)

1. **Clonar e entrar no projeto**

   ```bash
   git clone https://github.com/iagoomes/btg-orders-challenge.git
   cd orders-service
   ```
2. **Subir tudo com Docker Compose** (Postgres + RabbitMQ + App)

   ```bash
   docker compose up -d
   ```
3. **Acessar**

    * Swagger UI: [http://localhost:8080/btg-orders/swagger-ui.html](http://localhost:8080/btg-orders/swagger-ui.html)
    * Healthcheck: [http://localhost:8080/btg-orders/actuator/health](http://localhost:8080/btg-orders/actuator/health)
    * RabbitMQ UI: [http://localhost:15672](http://localhost:15672) (guest/guest)
    * pgAdmin (opcional): [http://localhost:8081](http://localhost:8081) ([admin@btg.com](mailto:admin@btg.com) / admin123)

> **Dica:** para incluir o pgAdmin, use o perfil `tools`: `docker compose --profile tools up -d`

---

## Passo a passo para o uso do APP

### 0) Pré‑requisitos
* Portas livres: **5432**, **5672**, **15672**, **8080**, **8081**

### 1) Subir o ambiente

**🔹 Opção 1 — Docker Compose (ambiente completo)**

```bash
git clone https://github.com/iagoomes/btg-orders-challenge.git
cd orders-service
docker compose up -d
```

Acesse:
- Swagger UI: http://localhost:8080/btg-orders/swagger-ui.html
- Healthcheck: http://localhost:8080/btg-orders/actuator/health
- RabbitMQ UI: http://localhost:15672 (guest/guest)
- pgAdmin: http://localhost:8081 (admin@btg.com / admin123)

**🔹 Opção 2 — Imagem no Docker Hub (execução simplificada)**

```bash
docker pull freshiagoomes/btg-orders-service:latest

# Criar rede
docker network create btg-network

# Subir PostgreSQL
docker run -d --name postgres --network btg-network \
  -e POSTGRES_DB=orders_db -e POSTGRES_USER=orders_user \
  -e POSTGRES_PASSWORD=orders_pass postgres:16-alpine

# Subir aplicação (conectando ao Postgres)
docker run -d --name app --network btg-network -p 8080:8080 \
  -e DB_HOST=postgres freshiagoomes/btg-orders-service:latest
```

Acesse: http://localhost:8080/btg-orders/swagger-ui.html

**🔹 Opção 3 — Build local**

```bash
git clone https://github.com/iagoomes/btg-orders-challenge.git
cd orders-service
mvn clean package -DskipTests
docker build -t btg-orders-service:latest .
docker compose up -d
```

* Aguarde os *healthchecks* ficarem **healthy**:

  ```bash
  docker compose ps
  # ou
  curl -s http://localhost:8080/btg-orders/actuator/health
  ```

### 2) Verificar o RabbitMQ

* Acesse **[http://localhost:15672](http://localhost:15672)** (guest/guest)
* Vá em **Queues & Streams** e confira se as filas existem:

    * `orders.queue` (processamento)
    * `orders.dlq` (dead-letter)
* A *exchange* `orders.exchange` e *routing key* `orders.process` estão configuradas para rotear mensagens para `orders.queue`.

### 3) Publicar uma mensagem de teste

* Em **Queues → orders.queue → Publish message**:

    * **Content type**: `application/json`
    * **Payload** (exemplo):

      ```json
      {
        "codigoPedido": 1001,
        "codigoCliente": 1,
        "itens": [
          { "produto": "lápis", "quantidade": 100, "preco": 1.10 }
        ]
      }
      ```
* Alternativa via CLI (amqplib-tools/k6/amqp) não é necessária; a UI cobre o teste.

### 4) Consultar as APIs

* **Total do pedido**

  ```bash
  curl -s http://localhost:8080/btg-orders/api/v1/orders/1001/total | jq
  ```

  **Resposta (exemplo):**

  ```json
  { "orderId": 1001, "total": 110.0, "currency": "BRL" }
  ```

* **Quantidade de pedidos do cliente**

  ```bash
  curl -s http://localhost:8080/btg-orders/api/v1/customers/1/orders/count | jq
  ```

  **Resposta (exemplo):**

  ```json
  { "customerId": 1, "orderCount": 1 }
  ```

* **Listagem paginada de pedidos do cliente**

  ```bash
  curl -s "http://localhost:8080/btg-orders/api/v1/customers/1/orders?page=0&size=10" | jq
  ```

  **Resposta (exemplo):**

  ```json
  {
    "customerId": 1,
    "orders": [
      {
        "orderId": 1001,
        "customerId": 1,
        "totalAmount": "110.00",
        "itemsCount": 1,
        "createdAt": "2024-03-15T10:30:00Z",
        "items": [ { "product": "pencil", "quantity": 100, "price": 1.10 } ]
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
  ```

> **Observação:** Os exemplos acima ilustram o contrato. O cálculo do total considera soma `quantidade × preço` por item.

### 5) Explorar no Swagger

* Abra: **[http://localhost:8080/btg-orders/swagger-ui.html](http://localhost:8080/btg-orders/swagger-ui.html)**
* Execute os mesmos endpoints pela interface (com *Try it out*).

### 6) (Opcional) Inspecionar no pgAdmin

* **URL:** [http://localhost:8081](http://localhost:8081)
  **Login:** `admin@btg.com` / `admin123`
* Configure um *server* apontando para `postgres:5432` com usuário `orders_user` e senha `orders_pass`.

---

## APIs REST (exemplos)

**Base URL:** `http://localhost:8080/btg-orders`

| Método | Endpoint                                       | Descrição                         |
| ------ | ---------------------------------------------- | --------------------------------- |
| GET    | `/api/v1/orders/{order_id}/total`              | Valor total do pedido             |
| GET    | `/api/v1/customers/{customer_id}/orders/count` | Quantidade de pedidos por cliente |
| GET    | `/api/v1/customers/{customer_id}/orders`       | Lista de pedidos (paginada)       |

### Esquemas principais

* **OrderTotalResponse** `{ orderId, total, currency }`
* **CustomerOrderCountResponse** `{ customerId, orderCount }`
* **CustomerOrdersResponse** `{ customerId, orders[], totalElements, totalPages, currentPage, pageSize }`
* **OrderSummary** `{ orderId, customerId, totalAmount, itemsCount, createdAt, items[] }`
* **OrderItemSummary** `{ product, quantity, price }`

> A especificação completa está em [`openapi.yaml`](./openapi.yaml) e é servida pelo Swagger UI.

---

## Mensageria (formato de mensagem)

**Queue:** `orders.queue`
**Exchange:** `orders.exchange`
**Routing key:** `orders.process`
**DLQ:** `orders.dlq`

**Payload (exemplo mínimo):**

```json
{
  "codigoPedido": 1001,
  "codigoCliente": 1,
  "itens": [
    { "produto": "lápis", "quantidade": 100, "preco": 1.10 }
  ]
}
```

---

## Configuração (variáveis e perfis)

| Variável              | Descrição                 | Default (Compose) |
| --------------------- | ------------------------- | ----------------- |
| `DB_HOST`             | Host do PostgreSQL        | `postgres`        |
| `DB_PORT`             | Porta do PostgreSQL       | `5432`            |
| `DB_NAME`             | Nome do DB                | `orders_db`       |
| `DB_USERNAME`         | Usuário do DB             | `orders_user`     |
| `DB_PASSWORD`         | Senha do DB               | `orders_pass`     |
| `RABBITMQ_HOST`       | Host do RabbitMQ          | `rabbitmq`        |
| `RABBITMQ_PORT`       | Porta AMQP                | `5672`            |
| `RABBITMQ_USERNAME`   | Usuário                   | `guest`           |
| `RABBITMQ_PASSWORD`   | Senha                     | `guest`           |
| `JPA_DDL_AUTO`        | Estratégia DDL            | `update`          |
| `JPA_SHOW_SQL`        | Log de SQL                | `false`           |
| `LOG_LEVEL_APP`       | Log da app                | `INFO`            |
| `LOG_LEVEL_SQL`       | Log SQL                   | `WARN`            |
| `LOG_LEVEL_ROOT`      | Log root                  | `INFO`            |
| `ORDERS_QUEUE_NAME`   | Nome da fila              | `orders.queue`    |
| `ORDERS_EXCHANGE`     | Exchange                  | `orders.exchange` |
| `ORDERS_ROUTING_KEY`  | Routing key               | `orders.process`  |
| `ORDERS_DLQ`          | Dead-letter queue         | `orders.dlq`      |
| `ORDERS_DLX`          | Dead-letter exchange      | `orders.dlx`      |
| `HEALTH_SHOW_DETAILS` | Exibir detalhes do health | `always`          |
| `SWAGGER_ENABLED`     | Habilitar Swagger         | `true`            |
| `SERVER_PORT`         | Porta HTTP                | `8080`            |

**Perfis:**

* `default` — desenvolvimento local
* `docker` — execução containerizada
* `test` — execução de testes

---

## Qualidade e Testes

* **Cobertura**: meta de 100% (classes, métodos, linhas e branches)
* **Unitários**: regras de negócio e mapeadores
* **Integração**: providers (DB/MQ) e configurações
* **Relatório Jacoco**:

  ```bash
  mvn clean test jacoco:report
  open target/site/jacoco/index.html
  ```

**Smoke tests via cURL**

```bash
# Health
curl -s http://localhost:8080/btg-orders/actuator/health | jq

# Endpoints
curl -s http://localhost:8080/btg-orders/api/v1/orders/1001/total | jq
curl -s http://localhost:8080/btg-orders/api/v1/customers/1/orders/count | jq
curl -s "http://localhost:8080/btg-orders/api/v1/customers/1/orders?page=0&size=10" | jq
```

---

## Docker / Compose

* **Subir**: `docker compose up -d`
* **Logs**: `docker compose logs -f orders-service`
* **Rebuild**: `docker compose build --no-cache orders-service && docker compose up -d`
* **Parar**: `docker compose down -v` (remove volumes)

**Imagens utilizadas**: `postgres:16-alpine`, `rabbitmq:3.13-management-alpine`, `dpage/pgadmin4:latest` e `btg-orders-service:latest` (build local via `Dockerfile`).

> O Compose provisiona dados persistentes (volumes) e carrega *definitions* do RabbitMQ e scripts de *init* do Postgres.

---

## Swagger UI

* **UI**: [http://localhost:8080/btg-orders/swagger-ui.html](http://localhost:8080/btg-orders/swagger-ui.html)
* **OpenAPI JSON**: [http://localhost:8080/btg-orders/api-docs](http://localhost:8080/btg-orders/api-docs)

Para desenvolvimento *contract-first*, edite `openapi.yaml` e gere código com o plugin do **OpenAPI Generator** durante o `mvn compile`.

---

## Troubleshooting

* **Portas em uso**: verifique `lsof -i :8080` (macOS) ou `netstat -ano | findstr 8080` (Windows). Libere ou altere as portas.
* **Health não fica UP**: inspeccione logs do serviço `orders-service` e confirme conectividade com DB/MQ.
* **RabbitMQ sem filas**: reinicie com `docker compose down -v && docker compose up -d` para reprovisionar *definitions*.
* **pgAdmin não conecta**: use host `postgres`, porta `5432`, usuário `orders_user`, senha `orders_pass`.

---

## OpenAPI + Generator (Delegate Pattern)

Quando executado `mvn clean compile`, o plugin gera:

* **Interface API** (ex.: `CustomersApi`) — contratos dos endpoints
* **Controller** (ex.: `CustomersApiController`) — recebe HTTP e delega
* **Delegate Interface** (ex.: `CustomersApiDelegate`) — ponto de extensão

**Implementação manual**: classes `app.resource` implementam os Delegates, chamando **Services** (aplicação), **UseCases** (domínio) e **Providers** (infra). Essa abordagem garante **separação clara** entre código gerado e de negócio, documentação **sempre atualizada** e **validação automática** de requests conforme a spec.

---

**Versão:** 1.0.0
**Docker Registry:** `freshiagoomes/btg-orders-service`
**Desafio:** BTG Pactual
