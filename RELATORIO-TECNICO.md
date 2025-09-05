# 📊 Relatório Técnico Final - BTG Orders Service

**Candidato**: Iago Gomes Antonio  
**Email**: iagoomes@outlook.com  
**GitHub**: https://github.com/iagoomes/btg-orders-challenge  
**Docker Hub**: https://hub.docker.com/r/freshiagoomes/btg-orders-service  
**Data de Entrega**: 05/09/2025 (2 dias antes do prazo)  

---

## I. 📅 Plano de Trabalho (Previsto vs Realizado)

| Dia | Planejado | Realizado | Status |
|-----|-----------|-----------|---------|
| **1** | Setup + Modelagem (7h) | ✅ Plano + Arquitetura | **CONCLUÍDO** |
| **2-3** | Core + APIs (14h) | ✅ Consumer + APIs | **CONCLUÍDO** |
| **4** | Testes (4h) | ✅ **ANTECIPADO** | **CONCLUÍDO** |
| **5** | Docker + Docs (7h) | ✅ **ENTREGUE** | **CONCLUÍDO** |

**Resultado**: Entregue 2 dias antes do prazo com 100% dos requisitos atendidos.

---

## II. 🛠️ Tecnologias Utilizadas

| Categoria | Tecnologia | Versão | Justificativa |
|-----------|------------|--------|---------------|
| **Linguagem** | Java | 21 (LTS) | Estabilidade enterprise |
| **Framework** | Spring Boot | 3.5.5 | Produtividade + ecossistema |
| **Banco** | PostgreSQL | 16 | JOINs necessários para relatórios |
| **Mensageria** | RabbitMQ | 3.13 | DLQ nativo + simplicidade |
| **Build** | Maven | 3.9+ | OpenAPI Generator |
| **Container** | Docker + Compose | - | Ambiente isolado |

### Ambiente de Desenvolvimento
- **IDE**: IntelliJ IDEA
- **SO**: macOS  
- **Versionamento**: Git + GitHub

### Decisões Estratégicas para Domínio Bancário

A abordagem de desenvolvimento foi cuidadosamente planejada considerando as **exigências específicas do setor financeiro**:

**OpenAPI-First Strategy**: Implementação orientada a especificações para garantir:
- **Contratos bem definidos** entre todas as partes interessadas
- **Reutilização eficiente** de recursos entre diferentes verticais de negócio
- **Alinhamento de stakeholders** desde o início do desenvolvimento
- **Automação de código** através do OpenAPI Generator, aumentando produtividade

**Banco de Dados Relacional (PostgreSQL)**: Escolha justificada por:
- **Consistência ACID** essencial para transações financeiras
- **Necessidade de JOINs** para geração de relatórios complexos
- **Similaridade com Oracle** para aproveitar conhecimento prévio
- **Robustez** para ambiente de produção bancária

**Containerização com Docker**: Estratégia para:
- **Ambiente controlado** e reproduzível
- **Facilitar deploy** em diferentes ambientes
- **Isolamento de dependências** para maior segurança
- **Padronização** de ambiente de desenvolvimento

Esta abordagem assegura que o projeto atenda aos **padrões de segurança e boas práticas** exigidos no **domínio bancário**.

---

## III. 🏗️ Diagrama de Arquitetura

### Clean Architecture (4 Camadas)

```
┌─────────────────────────────────────────┐
│           External Interfaces           │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │REST API │ │RabbitMQ │ │Postgres │   │
│  └─────────┘ └─────────┘ └─────────┘   │
└─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│        Infrastructure Layer            │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │Resources│ │Consumers│ │Providers│   │
│  └─────────┘ └─────────┘ └─────────┘   │
└─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│         Application Layer              │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │Services │ │Mappers  │ │DTOs     │   │
│  └─────────┘ └─────────┘ └─────────┘   │
└─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│           Domain Layer                  │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │UseCases │ │Entities │ │Contracts│   │
│  └─────────┘ └─────────┘ └─────────┘   │
└─────────────────────────────────────────┘
```

---

## IV. 📊 Modelagem da Base de Dados

```sql
-- Estrutura otimizada para os requisitos BTG
CREATE TABLE customers (
  customer_id BIGINT PRIMARY KEY,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
  order_id BIGINT PRIMARY KEY,
  customer_id BIGINT REFERENCES customers(customer_id),
  total_amount DECIMAL(10,2) NOT NULL,
  items_count INTEGER NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
  id BIGSERIAL PRIMARY KEY,
  order_id BIGINT REFERENCES orders(order_id),
  product VARCHAR(255) NOT NULL,
  quantity INTEGER NOT NULL CHECK (quantity > 0),
  price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
  total_price DECIMAL(10,2) GENERATED ALWAYS AS (quantity * price) STORED
);

-- Índices para performance das APIs BTG
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
```

---

## V. 🐳 Diagrama de Implantação

```
┌─────────────────────────────────────────┐
│           Docker Compose               │
│                                         │
│  ┌─────────────┐ ┌─────────────┐       │
│  │BTG Orders   │ │PostgreSQL   │       │
│  │Service      │ │    :5432    │       │
│  │   :8080     │ │             │       │
│  └─────────────┘ └─────────────┘       │
│          │              │              │
│  ┌─────────────┐ ┌─────────────┐       │
│  │RabbitMQ     │ │Docker       │       │
│  │:5672/:15672 │ │Network      │       │
│  └─────────────┘ └─────────────┘       │
└─────────────────────────────────────────┘
```

---

## VI. ☁️ Diagrama de Infraestrutura Cloud

```
┌─────────────────────────────────────────┐
│         Produção Sugerida              │
│                                         │
│  ┌─────────────┐ ┌─────────────┐       │
│  │Load         │ │ECS/EKS      │       │
│  │Balancer     │ │Cluster      │       │
│  └─────────────┘ └─────────────┘       │
│          │              │              │
│  ┌─────────────┐ ┌─────────────┐       │
│  │RDS          │ │Amazon MQ    │       │
│  │PostgreSQL   │ │RabbitMQ     │       │
│  └─────────────┘ └─────────────┘       │
└─────────────────────────────────────────┘
```

---

## VII. 🧪 Evidências de Testes Funcionais

### Cobertura Alcançada
- **336 testes** automatizados
- **100% cobertura** de código
- **Testes de integração** completos

### APIs BTG Testadas

```bash
# 1. Valor total do pedido
curl http://localhost:8080/btg-orders/api/v1/orders/1001/total
# ✅ Response: {"orderId": 1001, "totalValue": 110.00}

# 2. Quantidade de pedidos por cliente  
curl http://localhost:8080/btg-orders/api/v1/customers/1/orders/count
# ✅ Response: {"customerId": 1, "ordersCount": 2}

# 3. Lista de pedidos por cliente
curl "http://localhost:8080/btg-orders/api/v1/customers/1/orders?page=0&size=10"
# ✅ Response: Lista paginada completa
```

### Teste RabbitMQ
1. **Mensagem publicada** via Management UI
2. **Consumer processou** corretamente  
3. **Dados persistidos** no PostgreSQL
4. **APIs retornaram** dados corretos

---

## VIII. 🔗 Códigos no GitHub

**Repositório**: https://github.com/iagoomes/btg-orders-challenge

### Estrutura do Projeto
```
btg-orders-challenge/
├── README.md                    # Documentação principal
├── PLANO-DE-TRABALHO.md        # Plano BTG
├── RELATORIO-TECNICO.md        # Este relatório
├── docker-compose.yml          # Ambiente completo
├── src/main/java/              # Implementação
└── src/test/java/              # 336 testes
```

---

## IX. 📦 Imagens Docker Hub

**Perfil**: https://hub.docker.com/u/freshiagoomes  
**Imagem**: https://hub.docker.com/r/freshiagoomes/btg-orders-service:latest

### Deploy Rápido
```bash
# Opção 1: Docker Compose (recomendado)
docker compose up -d

# Opção 2: Imagem Docker Hub
docker pull freshiagoomes/btg-orders-service:latest
```

---

## X. 📚 Referências Utilizadas

### Documentação Oficial
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)  
- [OpenAPI Specification](https://swagger.io/specification/)

### Arquitetura
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)

---

## XI. 🎯 Itens Relevantes

### Diferenciais Técnicos
| Diferencial | Valor |
|-------------|-------|
| **Clean Architecture** | Manutenibilidade |
| **OpenAPI-First** | Qualidade de APIs |
| **100% Test Coverage** | Confiabilidade |
| **Docker Optimization** | Performance |

### Metodologias Aplicadas
- **TDD** - Testes antes da implementação
- **Contract-First** - Especificação antes do código
- **Clean Code** - Código legível

---

## 🏆 Resultado Final - Requisitos BTG

| Requisito BTG | Status | Evidência |
|---------------|---------|-----------|
| **1. Plano de trabalho** | ✅ | [PLANO-DE-TRABALHO.md](./PLANO-DE-TRABALHO.md) |
| **2. Aplicação Java** | ✅ | Spring Boot 3.5 + Java 21 |
| **3. Base PostgreSQL** | ✅ | JPA + Hibernate |
| **4. Microsserviço RabbitMQ** | ✅ | Consumer funcionando |
| **5. API REST (3 endpoints)** | ✅ | Swagger UI disponível |
| **6. Relatório técnico** | ✅ | Este documento |

### Entrega Antecipada
- **Prazo**: 7 dias corridos
- **Entregue**: 5 dias (2 dias de antecedência)
- **Qualidade**: Production-ready

---

**Status Final**: ✅ **DESAFIO BTG CONCLUÍDO COM SUCESSO**

**Desenvolvido por**: Iago Gomes Antonio  
**Para**: BTG Pactual - Desafio Técnico 2025
