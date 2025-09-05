# 📊 Relatório Técnico Final - BTG Orders Service Challenge

**Candidato**: Iago Gomes Antonio  
**Email**: iagoomes@outlook.com  
**GitHub**: https://github.com/iagoomes/btg-orders-challenge  
**Docker Hub**: https://hub.docker.com/r/freshiagoomes/btg-orders-service  
**Data de Entrega**: 05/09/2025 (2 dias antes do prazo)  
**Status**: ✅ **PROJETO CONCLUÍDO COM SUCESSO**

---

## I. 📅 Plano de Trabalho (Previsto vs Realizado)

### Resumo Executivo
O projeto foi **concluído com 2 dias de antecedência**, atingindo **100% dos requisitos obrigatórios** e entregando funcionalidades extras que demonstram conhecimento técnico avançado.

### Cronograma Executado

| Dia | Planejado | Realizado | Status |
|-----|-----------|-----------|---------|
| **1** | Setup + Modelagem (7h) | ✅ Plano + Arquitetura completa | **CONCLUÍDO** |
| **2-3** | Core (8h) | ✅ Consumer RabbitMQ + APIs | **CONCLUÍDO** |
| **4** | APIs REST (6h) | ✅ **PROJETO FINALIZADO** | **CONCLUÍDO** |
| **5** | Testes (4h) | ✅ **ENTREGUE COM ANTECEDÊNCIA** | **CONCLUÍDO** |

### Motivos do Sucesso Antecipado
1. **Estratégia OpenAPI-First** acelerou desenvolvimento
2. **Clean Architecture** facilitou implementação
3. **Experiência prévia** na stack escolhida
4. **Automação** via OpenAPI Generator

---

## II. 🛠️ Tecnologias Utilizadas

### Stack Principal
- **Java 21** (LTS) - Linguagem principal
- **Spring Boot 3.3.4** - Framework base
- **Spring Data JPA** - Persistência ORM
- **Spring AMQP** - Integração RabbitMQ
- **Maven 3.9+** - Build e dependências

### Infraestrutura
- **PostgreSQL 16-alpine** - SGBD relacional
- **RabbitMQ 3.13-management** - Message broker
- **Docker + Docker Compose** - Containerização

### Qualidade e Documentação
- **OpenAPI 3.0 + Swagger UI** - Especificação e documentação
- **JUnit 5 + Mockito** - Testes (332 testes, 100% coverage)
- **JaCoCo** - Cobertura de código

### Ambiente de Desenvolvimento
- **IDE**: IntelliJ IDEA
- **SO**: macOS
- **Git**: Versionamento no GitHub

---

## III. 🏗️ Diagrama de Arquitetura

### Clean Architecture (4 Camadas)

```mermaid
graph TB
    subgraph "External Interfaces"
        A[REST Clients]
        B[RabbitMQ Producers]
        C[Database]
    end
    
    subgraph "Infrastructure Layer"
        D[REST Controllers]
        E[Message Consumers]
        F[JPA Repositories]
        G[Configuration Beans]
    end
    
    subgraph "Application Layer"
        H[Resource Implementations]
        I[Service Orchestration]
        J[Mappers & DTOs]
    end
    
    subgraph "Domain Layer"
        K[Use Cases]
        L[Entities]
        M[Interfaces/Contracts]
    end
    
    A --> D
    B --> E
    D --> H
    E --> H
    H --> I
    I --> K
    K --> M
    F --> C
    I --> F
```

### Padrões Implementados
- **Clean Architecture** - Separação de responsabilidades
- **Repository Pattern** - Abstração de persistência
- **Use Case Pattern** - Encapsulamento de regras de negócio
- **Mapper Pattern** - Conversão entre camadas
- **Delegate Pattern** - Separação API gerada vs implementação

---

## IV. 📊 Modelagem da Base de Dados

### Modelo Relacional

```sql
-- Estrutura principal
CREATE TABLE customers (
  customer_id BIGINT PRIMARY KEY,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
  order_id BIGINT PRIMARY KEY,
  customer_id BIGINT REFERENCES customers(customer_id),
  total_amount DECIMAL(10,2) NOT NULL,
  items_count INTEGER NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT REFERENCES orders(order_id),
  product VARCHAR(255) NOT NULL,
  quantity INTEGER NOT NULL CHECK (quantity > 0),
  price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
  total_price DECIMAL(10,2) GENERATED ALWAYS AS (quantity * price) STORED
);

-- Índices para performance
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
```

### Decisões de Design
| Decisão | Justificativa |
|---------|---------------|
| **PostgreSQL** | Necessidade de JOINs para relatórios complexos |
| **Chaves naturais** | order_id e customer_id são códigos de negócio |
| **Campos calculados** | total_price garantido pela base |
| **Índices estratégicos** | Baseados nas consultas das APIs |

---

## V. 🐳 Diagrama de Implantação

### Arquitetura Docker

```mermaid
graph LR
    subgraph "Docker Compose Environment"
        subgraph "Application Container"
            A[BTG Orders Service<br/>Port: 8080<br/>Java 21 + Spring Boot]
        end
        
        subgraph "Database Container"
            B[PostgreSQL 16<br/>Port: 5432<br/>Data persistence]
        end
        
        subgraph "Message Broker Container"
            C[RabbitMQ 3.13<br/>Port: 5672, 15672<br/>Management UI]
        end
    end
    
    subgraph "External Access"
        D[REST Clients]
        E[RabbitMQ Publishers]
        F[Database Admin]
    end
    
    D --> A
    E --> C
    F --> B
    A --> B
    A --> C
```

### Configuração de Deploy
- **Network**: `orders-network` (bridge)
- **Volumes**: Persistência de dados PostgreSQL
- **Health Checks**: Todos os containers monitorados
- **Environment**: Variáveis externalizadas

---

## VI. ☁️ Diagrama de Infraestrutura Cloud

### Arquitetura de Produção Sugerida

```mermaid
graph TB
    subgraph "Load Balancer"
        A[Application Load Balancer]
    end
    
    subgraph "Application Tier"
        B[ECS/Kubernetes Cluster]
        C[BTG Orders Service<br/>Auto Scaling]
    end
    
    subgraph "Database Tier"
        D[RDS PostgreSQL<br/>Multi-AZ]
        E[Read Replicas]
    end
    
    subgraph "Message Tier"
        F[Amazon MQ<br/>RabbitMQ]
    end
    
    subgraph "Monitoring"
        G[CloudWatch/Grafana]
        H[ELK Stack]
    end
    
    A --> B
    B --> C
    C --> D
    C --> E
    C --> F
    C --> G
    C --> H
```

### Recursos Cloud Utilizados
- **Compute**: ECS/EKS para containers
- **Database**: RDS PostgreSQL com Multi-AZ
- **Messaging**: Amazon MQ (RabbitMQ)
- **Monitoring**: CloudWatch + ELK Stack
- **Storage**: EBS para persistência

---

## VII. 🧪 Evidências de Testes Funcionais

### Cobertura Alcançada
| Categoria | Quantidade | Cobertura |
|-----------|------------|-----------|
| **Testes Unitários** | 280+ | 100% |
| **Testes Integração** | 50+ | 100% |
| **TOTAL** | **332 testes** | **100%** |

### Testes Funcionais das APIs

#### 1. Valor Total do Pedido
```bash
curl http://localhost:8080/btg-orders/api/v1/orders/1001/total
# ✅ Response: {"orderId": 1001, "totalValue": 110.00, "itemsCount": 1}
```

#### 2. Quantidade de Pedidos por Cliente
```bash
curl http://localhost:8080/btg-orders/api/v1/customers/1/orders/count
# ✅ Response: {"customerId": 1, "ordersCount": 2}
```

#### 3. Lista de Pedidos Paginada
```bash
curl "http://localhost:8080/btg-orders/api/v1/customers/1/orders?page=0&size=10"
# ✅ Response: Lista paginada completa
```

### Teste de Mensageria RabbitMQ
1. **Mensagem publicada** via Management UI
2. **Consumer processou** corretamente
3. **Dados salvos** no PostgreSQL
4. **APIs retornaram** dados corretos

---

## VIII. 🔗 Códigos no GitHub

### Repositório Principal
**URL**: https://github.com/iagoomes/btg-orders-challenge

### Estrutura do Repositório
```
btg-orders-challenge/
├── README.md                    # Documentação principal
├── PLANO-DE-TRABALHO.md        # Plano original
├── RELATORIO-TECNICO.md        # Este relatório
├── DOCUMENTACAO-TECNICA.md      # Detalhes técnicos
├── docker-compose.yml          # Ambiente completo
├── Dockerfile                  # Build da aplicação
├── pom.xml                     # Dependências Maven
├── src/                        # Código fonte
│   ├── main/java/              # Implementação
│   └── test/java/              # Testes (332 testes)
└── target/                     # Artefatos compilados
```

---

## IX. 📦 Imagens Docker Hub

### Perfil Docker Hub
**URL**: https://hub.docker.com/u/freshiagoomes

### Imagem da Aplicação
**URL**: https://hub.docker.com/r/freshiagoomes/btg-orders-service:latest
- **Size**: ~200MB (otimizada)
- **Multi-stage build**: Redução de tamanho
- **Health checks**: Monitoramento integrado

### Comandos de Deploy
```bash
# Opção 1: Docker Compose (recomendado)
docker compose up -d

# Opção 2: Imagem Docker Hub
docker pull freshiagoomes/btg-orders-service:latest

# Opção 3: Build local
mvn clean package -DskipTests
docker build -t btg-orders-service .
```

---

## X. 📚 Referências Utilizadas

### Documentação Oficial
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [OpenAPI Specification](https://swagger.io/specification/)

### Arquitetura e Padrões
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)
- [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)

### Best Practices
- [Spring Boot Best Practices](https://springframework.guru/spring-boot-best-practices/)
- [JPA Best Practices](https://thoughts-on-java.org/jpa-hibernate-best-practices/)
- [REST API Design Guidelines](https://restfulapi.net/)

---

## XI. 🎯 Itens Adicionais Relevantes

### Funcionalidades Extras Implementadas
| Extra | Valor Técnico |
|-------|---------------|
| **Clean Architecture** | Manutenibilidade enterprise |
| **OpenAPI-First** | Qualidade de APIs |
| **100% Test Coverage** | Confiabilidade |
| **Swagger UI** | Developer Experience |
| **Health Checks** | Observabilidade |
| **Docker Optimization** | Performance |

### Framework de Testes
- **JUnit 5**: Testes unitários modernos
- **Mockito**: Mock objects para isolamento
- **Spring Boot Test**: Testes de integração
- **Testcontainers**: Testes com containers reais

### Metodologias Aplicadas
- **TDD (Test-Driven Development)**: Testes antes da implementação
- **Contract-First**: Especificação antes do código
- **GitFlow**: Versionamento organizado
- **Clean Code**: Código legível e manutenível

---

## 🏆 Resultados Finais

### ✅ Requisitos BTG - 100% Atendidos

| Requisito | Status | Evidência |
|-----------|---------|-----------|
| **Aplicação Java** | ✅ COMPLETO | Spring Boot 3.3 + Java 21 |
| **Base de dados** | ✅ COMPLETO | PostgreSQL + JPA |
| **Microsserviço RabbitMQ** | ✅ COMPLETO | Consumer + DLQ funcionando |
| **API REST (3 endpoints)** | ✅ COMPLETO | Swagger UI disponível |
| **Docker funcionando** | ✅ COMPLETO | Docker Hub + Compose |
| **Repositório GitHub** | ✅ COMPLETO | Público com documentação |
| **Plano de trabalho** | ✅ COMPLETO | Entregue no prazo |
| **Relatório técnico** | ✅ COMPLETO | Este documento |

### 🚀 Diferenciais Entregues
1. **Entrega antecipada** - 2 dias antes do prazo
2. **Qualidade superior** - 100% test coverage
3. **Arquitetura enterprise** - Clean Architecture
4. **Documentação profissional** - Múltiplos níveis
5. **Ambiente plug-and-play** - Docker Compose completo

---

**Status Final**: ✅ **MISSÃO CUMPRIDA COM DISTINÇÃO**

**Desenvolvido por**: Iago Gomes Antonio  
**Para**: BTG Pactual - Desafio Técnico 2025  
**Resultado**: Projeto production-ready entregue com antecedência
