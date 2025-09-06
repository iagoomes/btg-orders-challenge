# 📚 Documentação Técnica Detalhada - BTG Orders Service

## 🎯 Visão Geral

Esta documentação complementa o [README.md](./README.md) e [RELATORIO-TECNICO.md](./RELATORIO-TECNICO.md) com detalhes técnicos específicos da implementação.

---

## 🏗️ Decisões Arquiteturais Detalhadas

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

## 📦 Estrutura de Pacotes

### Organização do Código

```
src/main/java/com/btg/challenge/orders/
├── app/                        # Application Layer
│   ├── mapper/                 # Conversores entre camadas
│   ├── resource/               # Implementações dos Resources
│   └── service/                # Orquestração de serviços
├── domain/                     # Domain Layer
│   ├── entity/                 # Entidades de negócio
│   ├── provider/               # Interfaces para infra
│   └── usecase/                # Casos de uso
└── infra/                      # Infrastructure Layer
    ├── config/                 # Configurações Spring
    ├── dataprovider/           # Implementações providers
    └── messaging/              # Consumers RabbitMQ
```

### Convenções de Nomenclatura

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| **Entities (Domain)** | `EntityName` | `Order`, `Customer` |
| **Entities (JPA)** | `EntityNameData` | `OrderData`, `CustomerData` |
| **Use Cases** | `VerbNounUseCase` | `GetOrderTotalUseCase` |
| **Providers** | `NounProvider` | `OrderProvider` |
| **Resources** | `NounResource` | `OrdersResource` |

---

## 🔐 Configurações de Segurança

### Validações Implementadas

```yaml
# application.yml - Configurações de validação
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
    show-sql: false
  
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when_authorized
```

### Aspectos de Segurança

1. **Input Validation**: Bean Validation nas entradas
2. **SQL Injection**: Proteção via JPA/Hibernate
3. **XSS Protection**: Escape automático em JSON
4. **CORS**: Configuração para ambientes específicos

---

## 📈 Configurações de Performance

### Connection Pool (HikariCP)

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
```

### Índices Estratégicos

```sql
-- Otimizações para as consultas mais frequentes
CREATE INDEX CONCURRENTLY idx_orders_customer_created 
  ON orders(customer_id, created_at DESC);

CREATE INDEX CONCURRENTLY idx_order_items_order_product 
  ON order_items(order_id, product);
```

---

## 🧪 Estratégia de Testes Detalhada

### Pirâmide de Testes

```
    ┌─────────────────┐  ← 10% E2E Tests
    │   E2E Tests     │    (API completa + Docker)
    │                 │
    ├─────────────────┤  ← 20% Integration Tests  
    │ Integration     │    (Repositories + Consumers)
    │    Tests        │
    ├─────────────────┤  ← 70% Unit Tests
    │   Unit Tests    │    (Use Cases + Mappers + Services)
    │                 │
    └─────────────────┘
```

### Configuração de Testes

```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

---

## 📊 Monitoramento e Observabilidade

### Health Checks Customizados

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Verificações customizadas de saúde
        return Health.up()
            .withDetail("database", "operational")
            .withDetail("rabbitmq", "operational")
            .build();
    }
}
```

### Métricas Customizadas

```java
@Component
public class OrderMetrics {
    private final Counter orderProcessedCounter;
    private final Timer orderProcessingTimer;
    
    // Implementação de métricas específicas
}
```

---

## 🔄 Fluxo de Processamento de Mensagens

### Configuração RabbitMQ

```yaml
# Configuração completa do RabbitMQ
rabbitmq:
  orders:
    queue: orders.queue
    exchange: orders.exchange
    routing-key: orders.process
    dlq: orders.dlq
    dlx: orders.dlx
    retry-attempts: 3
    retry-delay: 5000
```

### Dead Letter Queue Strategy

```java
@RabbitListener(queues = "orders.dlq")
public void handleFailedMessages(OrderMessage message) {
    // Log do erro e notificação para equipe de suporte
    log.error("Failed to process order: {}", message);
    // Implementar lógica de retry manual ou alertas
}
```

---

## 🚀 Otimizações Futuras

### Curto Prazo (1-2 sprints)
- [ ] Cache distribuído com Redis
- [ ] Métricas customizadas com Micrometer
- [ ] Circuit breaker com Resilience4j
- [ ] Rate limiting

### Médio Prazo (3-6 meses)
- [ ] Event sourcing para auditoria
- [ ] CQRS pattern implementation
- [ ] Distributed tracing
- [ ] Load testing automatizado

### Longo Prazo (6+ meses)
- [ ] Kubernetes deployment
- [ ] Service mesh (Istio)
- [ ] Multi-region deployment
- [ ] Advanced monitoring (Prometheus/Grafana)

---

## 📋 Troubleshooting Guide

### Problemas Comuns

| Problema | Causa Provável | Solução |
|----------|----------------|---------|
| **Connection refused** | PostgreSQL não iniciado | `docker compose up postgres` |
| **Queue not found** | RabbitMQ não configurado | Verificar `definitions.json` |
| **404 endpoints** | Context path incorreto | Usar `/btg-orders/api/v1/` |
| **Validation errors** | Payload inválido | Verificar OpenAPI spec |

### Logs Importantes

```bash
# Verificar logs da aplicação
docker compose logs btg-orders-service

# Verificar logs do RabbitMQ
docker compose logs rabbitmq

# Verificar logs do PostgreSQL
docker compose logs postgres
```

---

## 🔗 Links de Referência

### Clean Architecture & Design Patterns
- **[Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)**
- **[Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)**
- **[Microservices Patterns](https://microservices.io/patterns/)**

### Spring Boot & Java
- **[Spring Boot Best Practices](https://springframework.guru/spring-boot-best-practices/)**
- **[JPA Best Practices](https://thoughts-on-java.org/jpa-hibernate-best-practices/)**
- **[Testing Spring Boot Applications](https://spring.io/guides/gs/testing-web/)**

### DevOps & Containerização
- **[Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)**
- **[Docker Compose Reference](https://docs.docker.com/compose/compose-file/)**

---

## 👨‍💻 Suporte Técnico

Para dúvidas técnicas específicas sobre a implementação, consulte:

1. **README.md** - Guia de início rápido
2. **RELATORIO-TECNICO.md** - Análise completa do projeto
3. **Este documento** - Detalhes técnicos específicos
4. **Código fonte** - Implementação com comentários

**Contato**: iagoomes@outlook.com
