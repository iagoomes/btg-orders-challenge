# BTG Orders Service - Desafio Técnico

Microsserviço para processamento de pedidos desenvolvido seguindo padrões bancários de segurança e boas práticas. O sistema processa mensagens via RabbitMQ e disponibiliza APIs REST para consulta de informações agregadas.

## Estratégia de Desenvolvimento

A estratégia para o desenvolvimento foi utilizar padrões de projeto e desenvolvimento que se baseiam em segurança e boas práticas, como deve ser em um domínio bancário.

Visando a **reutilização de código**, o desenvolvimento foi orientado a especificações usando **OpenAPI Specification** como core. Com ela conseguimos definir nossos recursos REST de forma padronizada, permitindo que equipes de desenvolvimento e consumidores estejam alinhados sobre contratos e implementações. Esta abordagem facilita o entendimento entre áreas sobre recursos específicos, permitindo avaliar reutilização versus desenvolvimento de novas implementações. Além disso, ganha-se velocidade no desenvolvimento através do **OpenAPI Generator Plugin**, que automatiza a criação de código baseado nas especificações.

### OpenAPI Generator Plugin - Arquitetura de Implementação

O **OpenAPI Generator** é uma ferramenta poderosa que gera automaticamente código a partir de especificações OpenAPI/Swagger. Em nosso projeto, utilizamos o padrão **Delegate Pattern** para máxima flexibilidade.

#### Como Funciona no Projeto

Quando executamos o plugin através do Maven (`mvn clean compile`), ele gera automaticamente as seguintes classes/interfaces:

1. **Interface API** (ex: `CustomersApi`)
    - Define os contratos dos endpoints
    - Contém as assinaturas dos métodos HTTP

2. **Controller** (ex: `CustomersApiController`)
    - Implementa a interface API
    - Delega as chamadas para o Delegate

3. **Delegate Interface** (ex: `CustomersApiDelegate`)
    - Interface para implementação da lógica de negócio
    - Permite separação clara entre código gerado e implementação

#### Exemplo de Estrutura Gerada:

```java
// Interface gerada
public interface CustomersApi {
    default CustomersApiDelegate getDelegate() {
        return new CustomersApiDelegate() {};
    }
    // métodos dos endpoints...
}

// Controller gerado
@RestController
public class CustomersApiController implements CustomersApi {
    private final CustomersApiDelegate delegate;
    // implementação...
}

// Delegate interface gerada
public interface CustomersApiDelegate {
    // métodos para implementar
}
```

#### Nossa Implementação:

Para implementar a lógica de negócio, criamos classes Resource em `app.resource` que implementam os Delegates:

```java
@Component
public class CustomersResource implements CustomersApiDelegate {
    // Implementação real da lógica de negócio
}
```

**Benefícios principais:**
- **Contract-First Development**: API é definida primeiro, implementação depois
- **Separação Clara**: Código gerado vs código manual
- **Consistency**: Garante alinhamento entre especificação e código
- **Documentation**: Swagger UI automático e sempre atualizado
- **Flexibility**: Delegate pattern permite customização total da implementação
- **Validation**: Requests validados automaticamente conforme spec

## Arquitetura e Boas Práticas

### Clean Architecture
Aplicação segue os princípios de Clean Architecture com separação clara de responsabilidades:
- **Domain**: Entidades e regras de negócio puras
- **Application**: Orquestração e casos de uso
- **Infrastructure**: Implementações de banco, filas, configurações

### Padrões Implementados
- **Delegate Pattern**: Separação entre código gerado e implementação manual
- **Dependency Inversion**: Domain define interfaces, Infrastructure implementa
- **Repository Pattern**: Abstração do acesso a dados
- **Mapper Pattern**: Conversão isolada entre camadas
- **Use Case Pattern**: Encapsulamento de regras de negócio
- **Provider Pattern**: Abstração de serviços externos

### Fluxo Arquitetural: API → Delegate → Resource → Service → UseCase → Provider

```
API Interface → API Controller → API Delegate → Resource Implementation → Service → UseCase → Provider → Database
      ↓              ↓                ↓               ↓                    ↓         ↓          ↓
  Generated      Generated        Generated        Manual              Manual    Manual     Manual
 (OpenAPI)       (OpenAPI)        (OpenAPI)    Implementation                Implementation
```

**Detalhamento do Fluxo:**

1. **API Interface** (Gerado)
    - Interface com contratos dos endpoints
    - Define métodos HTTP e assinaturas

2. **API Controller** (Gerado)
    - Implementação REST do Spring
    - Delega chamadas para o Delegate

3. **API Delegate** (Gerado)
    - Interface para implementação customizada
    - Ponto de extensão para lógica de negócio

4. **Resource** (Manual - `app.resource`)
    - Implementação do Delegate
    - Orquestração entre API e Services

5. **Application Service** (Manual - `app.service`)
    - Lógica de aplicação
    - Conversão entre DTOs e entidades de domínio

6. **Domain UseCase** (Manual - `domain.usecase`)
    - Regras de negócio puras
    - Validações e lógica de domínio

7. **Data Provider** (Manual - `infra.dataprovider`)
    - Implementação de acesso a dados
    - Isolamento da infraestrutura

### Stack Tecnológica
- **Java 21 LTS** + **Spring Boot 3.3** - Base sólida e moderna
- **PostgreSQL 16** - Banco relacional para consultas complexas e JOINs
- **RabbitMQ 3.13** - Message broker para processamento assíncrono
- **OpenAPI 3.0** + **Generator Plugin** - Contract-first development
- **Docker** + **Compose** - Containerização completa

## Início Rápido

```bash
# 1. Clonar repositório
git clone https://github.com/iagoomes/btg-orders-challenge.git
cd btg-orders-service

# 2. Gerar código do OpenAPI
mvn clean compile

# 3. Executar aplicação completa
docker-compose up -d

# 4. Verificar funcionamento
curl http://localhost:8080/btg-orders/actuator/health
```

**Aplicação disponível em:** http://localhost:8080/btg-orders/swagger-ui.html

## Índice de Temas

### 📊 [APIs REST](#apis-rest)
- Endpoints de consulta
- Paginação e filtros
- Contratos OpenAPI

### 📖 [Documentação Swagger](#swagger-ui)
- Interface interativa
- Teste de APIs
- Especificações automáticas

### 🐳 [Docker e Containerização](#docker)
- Ambiente completo
- Registry público
- Instruções de deploy

### 🐰 [Mensageria RabbitMQ](#mensageria)
- Processamento assíncrono
- Dead Letter Queue
- Formato de mensagens

### 🏗️ [Arquitetura](#arquitetura-detalhada)
- Clean Architecture
- Delegate Pattern
- Estrutura de camadas

### 🧪 [Testes e Qualidade](#testes)
- 100% cobertura
- Testes unitários e integração
- Relatórios de qualidade

### ⚙️ [Configuração](#configuração)
- Variáveis de ambiente
- Profiles por ambiente
- Monitoramento

## APIs REST

### Endpoints Principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/orders/{id}/total` | Valor total do pedido |
| GET | `/api/v1/customers/{id}/orders/count` | Quantidade de pedidos por cliente |
| GET | `/api/v1/customers/{id}/orders` | Lista de pedidos (paginada) |

**Base URL:** `http://localhost:8080/btg-orders`

## Swagger UI

Acesse a documentação interativa completa:
- **URL:** http://localhost:8080/btg-orders/swagger-ui.html
- **API Docs:** http://localhost:8080/btg-orders/api-docs

## Docker

### Imagem Pública
```bash
docker pull freshiagoomes/btg-orders-service:latest
```

**Docker Hub:** https://hub.docker.com/r/freshiagoomes/btg-orders-service

### Ambiente Completo
O docker-compose.yml inclui todos os serviços necessários:
- PostgreSQL com dados persistentes
- RabbitMQ com management UI
- Aplicação Spring Boot
- pgAdmin para administração

## Mensageria

**Formato da mensagem processada:**
```json
{
  "codigoPedido": 1001,
  "codigoCliente": 1,
  "itens": [
    {
      "produto": "lápis",
      "quantidade": 100,
      "preco": 1.10
    }
  ]
}
```

**Configuração:**
- Queue: `orders.queue`
- Exchange: `orders.exchange`
- Dead Letter Queue: `orders.dlq`

## Arquitetura Detalhada

### Estrutura de Pacotes
```
src/main/java/com/btg/challenge/orders/
├── api/                    # Classes geradas pelo OpenAPI
│   ├── CustomersApi.java
│   ├── CustomersApiController.java
│   ├── CustomersApiDelegate.java
│   └── ...
├── app/                    # Application Layer
│   ├── resource/          # Implementações dos Delegates
│   │   ├── CustomersResource.java
│   │   ├── OrdersResource.java
│   │   └── HealthResource.java
│   ├── service/           # Services de aplicação
│   └── mapper/            # Mappers entre camadas
├── domain/                 # Domain Layer
│   ├── entity/            # Entidades de domínio
│   ├── usecase/           # Casos de uso
│   └── [interfaces]       # Contratos do domínio
├── infra/                  # Infrastructure Layer
│   ├── config/            # Configurações
│   ├── dataprovider/      # Implementação providers
│   ├── mqprovider/        # RabbitMQ consumers
│   └── repository/        # JPA repositories
└── OrdersServiceApplication.java
```

### Separação de Responsabilidades

#### Camada API (Gerada)
- **Responsabilidade**: Contratos REST e delegação
- **Componentes**: Interfaces, Controllers e Delegates gerados pelo OpenAPI

#### Camada Application
- **Responsabilidade**: Orquestração e coordenação
- **app.resource**: Implementação dos Delegates, ponte entre API e Services
- **app.service**: Lógica de aplicação e coordenação de use cases
- **app.mapper**: Conversão entre DTOs e entidades de domínio

#### Camada Domain
- **Responsabilidade**: Lógica de negócio pura
- **Independente de frameworks e infraestrutura**
- **Define interfaces que a infraestrutura deve implementar**

#### Camada Infrastructure
- **Responsabilidade**: Implementações técnicas
- **Implementa as interfaces definidas pelo domínio**
- **Gerencia aspectos técnicos: DB, MQ, Config**

## Testes

Cobertura completa de **100%** em todas as métricas:
- Classes, Métodos, Linhas e Branches
- Testes unitários para toda lógica de negócio
- Testes de integração para providers
- Testes de configuração para setup Docker

```bash
mvn test jacoco:report
```

## Configuração

### Variáveis Principais
- `DB_HOST` - Host do PostgreSQL
- `RABBITMQ_HOST` - Host do RabbitMQ
- `LOG_LEVEL_APP` - Nível de log da aplicação
- `JPA_DDL_AUTO` - Estratégia DDL do Hibernate

### Profiles
- **default** - Desenvolvimento local
- **docker** - Ambiente containerizado
- **test** - Execução de testes

## URLs de Acesso

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| Aplicação | http://localhost:8080/btg-orders | - |
| Swagger UI | http://localhost:8080/btg-orders/swagger-ui.html | - |
| RabbitMQ | http://localhost:15672 | guest/guest |
| pgAdmin | http://localhost:8081 | admin@btg.com/admin123 |

---

**Versão:** 1.0.0 | **Docker Registry:** freshiagoomes/btg-orders-service | **Desafio:** BTG Pactual