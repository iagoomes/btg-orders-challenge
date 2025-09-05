# 📚 Documentação Técnica - BTG Orders Service

## 🎯 Visão Geral do Projeto

Este projeto demonstra a implementação de um **microsserviço** baseado em um app bancário seguindo padrões enterprise para processamento de pedidos. O sistema utiliza **mensageria assíncrona** via RabbitMQ e expõe **APIs REST** para consulta de dados agregados.

### Principais Características

- **Arquitetura Limpa (Clean Architecture)** com separação de responsabilidades
- **Contract-First Development** usando OpenAPI 3.0
- **Padrão Delegate** para separar código gerado do código de negócio
- **Mensageria robusta** com Dead Letter Queue (DLQ)
- **100% de cobertura de testes** unitários e de integração
- **Containerização completa** com Docker e Docker Compose

---

## 🏗️ Arquitetura e Design Patterns

### Clean Architecture

O projeto segue os princípios da **Clean Architecture**

```
┌─────────────────────────────────────────────────────────┐
│                    External Interfaces                 │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐   │
│  │   REST API  │ │  RabbitMQ   │ │   PostgreSQL    │   │
│  └─────────────┘ └─────────────┘ └─────────────────┘   │
└─────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────┐
│                 Infrastructure Layer                   │
│     ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │
│     │  Resources  │ │  Providers  │ │ Repositories│    │
│     └─────────────┘ └─────────────┘ └─────────────┘    │
└─────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────┐
│                  Application Layer                     │
│     ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │
│     │  Services   │ │   Mappers   │ │   DTOs      │    │
│     └─────────────┘ └─────────────┘ └─────────────┘    │
└─────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer                        │
│     ┌─────────────┐ ┌─────────────┐ ┌─────────────┐    │
│     │  Use Cases  │ │  Entities   │ │ Interfaces  │    │
│     └─────────────┘ └─────────────┘ └─────────────┘    │
└─────────────────────────────────────────────────────────┘
```

### OpenAPI Specification

- **Arquivo**: [`openapi.yaml`](./openapi.yaml)
- **Swagger UI**: http://localhost:8080/btg-orders/swagger-ui.html
- **JSON**: http://localhost:8080/btg-orders/api-docs

### Padrões Implementados

1. **Clean Architecture** - Separação clara de responsabilidades
2. **Dependency Inversion** - Domain define interfaces, Infra implementa
3. **Repository Pattern** - Abstração do acesso a dados
4. **Mapper Pattern** - Conversão entre camadas
5. **Strategy Pattern** - Providers intercambiáveis
6. **Contract-First** - APIs geradas a partir de especificação
7. **Delegate Pattern** - Separação entre código gerado e implementação
8. **Provider Pattern** - Abstração de serviços externos
9. **Factory Pattern** - Criação de objetos complexos
10. **Observer Pattern** - Consumo de mensagens RabbitMQ

---

## 🛠️ Stack Tecnológica

### Core Framework
- **[Spring Boot 3.3.x](https://spring.io/projects/spring-boot)** - Framework principal
- **[Spring Data JPA](https://spring.io/projects/spring-data-jpa)** - Persistência de dados
- **[Spring AMQP](https://spring.io/projects/spring-amqp)** - Integração RabbitMQ
- **[Java 21](https://openjdk.org/projects/jdk/21/)** - Linguagem e JVM

### Database & Messaging
- **[PostgreSQL 16](https://www.postgresql.org/)** - Banco de dados relacional
- **[RabbitMQ 3.13](https://www.rabbitmq.com/)** - Message broker
- **[HikariCP](https://github.com/brettwooldridge/HikariCP)** - Connection pooling

### Documentation & API
- **[OpenAPI 3.0](https://swagger.io/specification/)** - Especificação de APIs
- **[OpenAPI Generator](https://openapi-generator.tech/)** - Geração de código
- **[Swagger UI](https://swagger.io/tools/swagger-ui/)** - Interface de documentação

### Testing & Quality
- **[JUnit 5](https://junit.org/junit5/)** - Framework de testes
- **[Mockito](https://site.mockito.org/)** - Mock objects

### DevOps & Containerization
- **[Docker](https://www.docker.com/)** - Containerização
- **[Docker Compose](https://docs.docker.com/compose/)** - Orquestração local
- **[Maven](https://maven.apache.org/)** - Build e dependências

---

## 💾 Decisões Arquiteturais

### Database Design

**PostgreSQL vs MongoDB**: Escolhido PostgreSQL devido à:
- Necessidade de JOINs para relatórios complexos
- Consistência ACID essencial para domínio financeiro
- Suporte robusto a transações
- Melhor performance para consultas relacionais

**Nomenclatura *Data**: Entidades JPA com sufixo `Data` (ex: `OrderData`) para diferenciação clara das entidades de domínio.

**Context Path**: `/btg-orders` para identificação do serviço + versionamento `/api/v1/` para flexibilidade.

### Messaging Architecture

**RabbitMQ vs Apache Kafka**: RabbitMQ foi escolhido por:
- Melhor adequação para padrões request-reply
- Suporte nativo a Dead Letter Queues
- Menor complexidade operacional
- Interface de management mais amigável

### Code Generation Strategy

**OpenAPI-First vs Code-First**: Abordagem OpenAPI-First para:
- Documentação sempre atualizada
- Contratos bem definidos
- Validação automática de requests/responses
- Facilita desenvolvimento paralelo de frontend/backend

---

## 📊 Modelo de Dados

### Diagrama ER

```sql
-- Estrutura principal das tabelas
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
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
```

### Regras de Negócio

1. **Customer**: Criado automaticamente se não existir
2. **Order**: Deve ter pelo menos 1 item válido
3. **OrderItem**: Quantidade > 0, Preço >= 0
4. **Cálculo Total**: Soma de (quantidade × preço) de todos os itens

---

## 🧪 Estratégia de Testes

### Pirâmide de Testes

```
    ┌─────────────────┐
    │   E2E Tests     │  ← API completa + Docker
    │                 │
    ├─────────────────┤
    │ Integration     │  ← Repositories + Consumers
    │    Tests        │
    ├─────────────────┤
    │   Unit Tests    │  ← Use Cases + Mappers + Services
    │                 │
    └─────────────────┘
```

### Cobertura por Camada

- **Domain Layer**: 100% - Use Cases e Entities
- **Application Layer**: 100% - Services e Mappers  
- **Infrastructure Layer**: 100% - Configurações e Providers
- **API Layer**: 100% - Resources e Exception Handlers



---

## 🔐 Aspectos de Segurança

### Validações Implementadas

1. **Input Validation**: Bean Validation nas entradas
2. **SQL Injection**: Proteção via JPA/Hibernate
3. **XSS Protection**: Escape automático em JSON
4. **CORS**: Configuração para ambientes específicos

### Próximas Implementações

- [ ] **JWT Authentication**: Tokens para autenticação
- [ ] **Rate Limiting**: Controle de requisições por IP
- [ ] **Field-level Encryption**: Dados sensíveis criptografados
- [ ] **Audit Trail**: Log de todas as operações

---

## 📈 Monitoramento e Observabilidade

### Health Checks

```yaml
# Endpoint: /actuator/health
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "rabbit": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}
```

### Métricas Disponíveis

- **JVM Metrics**: Memória, GC, Threads
- **Database Metrics**: Connection pool, query time
- **RabbitMQ Metrics**: Messages processed, queue size
- **HTTP Metrics**: Request count, response time

### Logging Strategy

```yaml
# Níveis de log por pacote
logging:
  level:
    com.btg.challenge.orders: INFO
    org.springframework.web: DEBUG
    org.hibernate.SQL: WARN
```

---

## 🎯 Próximos Passos

### Curto Prazo (1-2 sprints)
- [ ] Implementar autenticação JWT/OAuth2
- [ ] Adicionar rate limiting com Spring Cloud Gateway
- [ ] Métricas customizadas com Micrometer/Prometheus
- [ ] Circuit breaker com Resilience4j

### Médio Prazo (3-6 meses)
- [ ] Cache distribuído com Redis
- [ ] Event sourcing para auditoria completa
- [ ] Implementar CQRS pattern
- [ ] API de administração e relatórios

### Longo Prazo (6+ meses)
- [ ] Deploy em Kubernetes com Helm Charts
- [ ] CI/CD completo com GitHub Actions
- [ ] Distributed tracing com Jaeger/Zipkin
- [ ] Load testing automatizado com K6

---

## 📚 Referências e Links Úteis

### Documentação Oficial

- **[Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)**
- **[Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)**
- **[RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)**
- **[PostgreSQL Documentation](https://www.postgresql.org/docs/)**
- **[OpenAPI Specification](https://swagger.io/specification/)**

### Clean Architecture & Design Patterns

- **[Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)**
- **[Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)**
- **[Microservices Patterns](https://microservices.io/patterns/)**
- **[Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)**

### Best Practices

- **[Spring Boot Best Practices](https://springframework.guru/spring-boot-best-practices/)**
- **[JPA Best Practices](https://thoughts-on-java.org/jpa-hibernate-best-practices/)**
- **[RabbitMQ Best Practices](https://www.rabbitmq.com/best-practices.html)**
- **[REST API Design Guidelines](https://restfulapi.net/)**

### Testing & Quality

- **[Testing Spring Boot Applications](https://spring.io/guides/gs/testing-web/)**
- **[Testcontainers Documentation](https://www.testcontainers.org/)**
- **[JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)**

---

## 📄 License

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 👨‍💻 Autor

**Iago Gomes Antonio**  
📧 Email: [iagoomes@outlook.com](mailto:iagoomes@outlook.com)  
🐙 GitHub: [@iagoomes](https://github.com/iagoomes)  
🐳 Docker Hub: [freshiagoomes](https://hub.docker.com/u/freshiagoomes)  
💼 LinkedIn: [linkedin.com/in/iago-gomes-antonio](https://linkedin.com/in/iago-gomes-antonio)

---
