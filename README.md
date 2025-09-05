# 🏦 BTG Orders Service - Desafio Técnico

**Candidato**: Iago Gomes Antonio  
**Email**: iagoomes@outlook.com  
**GitHub**: https://github.com/iagoomes/btg-orders-challenge  
**Docker Hub**: https://hub.docker.com/r/freshiagoomes/btg-orders-service  

[![Java 21](https://img.shields.io/badge/java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3](https://img.shields.io/badge/spring--boot-3.3-brightgreen)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/docker-ready-blue)](https://hub.docker.com/r/freshiagoomes/btg-orders-service)
[![Coverage](https://img.shields.io/badge/coverage-100%25-success)](./target/site/jacoco)

---

## 📋 Documentação do Desafio BTG

Este repositório contém a solução completa para o **Desafio Técnico BTG Pactual** - um microsserviço para processamento de pedidos com mensageria RabbitMQ e APIs REST.

### 📚 Documentos do Projeto

| Documento | Descrição | Link |
|-----------|-----------|------|
| **Plano de Trabalho** | Planejamento inicial com tasks e estimativas | [📋 PLANO-DE-TRABALHO.md](./PLANO-DE-TRABALHO.md) |
| **Relatório Técnico** | Relatório final com resultados e análises | [📊 RELATORIO-TECNICO.md](./RELATORIO-TECNICO.md) |
| **Documentação Técnica** | Detalhes arquiteturais e implementação | [📚 DOCUMENTACAO-TECNICA.md](DOCUMENTACAO-TECNICA.md) |

---

## 🚀 Início Rápido

### Pré-requisitos
- Docker e Docker Compose
- Portas livres: 5432, 5672, 15672, 8080

### Executar a Aplicação

```bash
# 1. Clonar o repositório
git clone https://github.com/iagoomes/btg-orders-challenge.git
cd orders-service

# 2. Subir todos os serviços
docker compose up -d

# 3. Verificar status
curl http://localhost:8080/btg-orders/actuator/health
```

### Acessar Interfaces

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| **Swagger UI** | http://localhost:8080/btg-orders/swagger-ui.html | - |
| **RabbitMQ Management** | http://localhost:15672 | guest/guest |
| **Health Check** | http://localhost:8080/btg-orders/actuator/health | - |

---

## 📋 Requisitos Atendidos

### ✅ Atividades Obrigatórias

| Item | Requisito | Status | Evidência |
|------|-----------|---------|-----------|
| 1 | Plano de trabalho | ✅ **COMPLETO** | [PLANO-DE-TRABALHO.md](./PLANO-DE-TRABALHO.md) |
| 2 | Aplicação Java | ✅ **COMPLETO** | Spring Boot 3.3 + Java 21 |
| 3 | Base de dados | ✅ **COMPLETO** | PostgreSQL + JPA |
| 4 | Microsserviço RabbitMQ | ✅ **COMPLETO** | Consumer funcionando |
| 5 | API REST (3 endpoints) | ✅ **COMPLETO** | Swagger UI disponível |
| 6 | Relatório técnico | ✅ **COMPLETO** | [RELATORIO-TECNICO.md](./RELATORIO-TECNICO.md) |

### 🎯 APIs Implementadas

| Endpoint | Descrição | Exemplo |
|----------|-----------|---------|
| `GET /api/v1/orders/{id}/total` | Valor total do pedido | [Testar](http://localhost:8080/btg-orders/swagger-ui.html) |
| `GET /api/v1/customers/{id}/orders/count` | Quantidade de pedidos | [Testar](http://localhost:8080/btg-orders/swagger-ui.html) |
| `GET /api/v1/customers/{id}/orders` | Lista de pedidos (paginada) | [Testar](http://localhost:8080/btg-orders/swagger-ui.html) |

### 📨 Formato da Mensagem RabbitMQ

```json
{
  "codigoPedido": 1001,
  "codigoCliente": 1,
  "itens": [
    {
      "produto": "lápis",
      "quantidade": 100,
      "preco": 1.10
    },
    {
      "produto": "caderno",
      "quantidade": 10,
      "preco": 1.00
    }
  ]
}
```

---

## 🧪 Testando a Aplicação

### 1. Publicar Mensagem no RabbitMQ

1. Acesse http://localhost:15672 (guest/guest)
2. Vá em **Queues & Streams → orders.queue → Publish message**
3. Cole o JSON de exemplo acima
4. Clique em **Publish message**

### 2. Testar APIs REST

```bash
# Valor total do pedido
curl http://localhost:8080/btg-orders/api/v1/orders/1001/total

# Quantidade de pedidos por cliente
curl http://localhost:8080/btg-orders/api/v1/customers/1/orders/count

# Lista de pedidos paginada
curl "http://localhost:8080/btg-orders/api/v1/customers/1/orders?page=0&size=10"
```

### 3. Interface Swagger

Acesse http://localhost:8080/btg-orders/swagger-ui.html para testar as APIs interativamente.

---

## 🛠️ Stack Tecnológica

## 🧪 Testes Opcionais (Para Desenvolvedores)

> **⚠️ IMPORTANTE**: Os testes abaixo são **totalmente opcionais** e **NÃO fazem parte do deploy**. O deploy é otimizado para ser rápido (~30-45 segundos).

### Script Automatizado de Testes

Para desenvolvedores que desejam executar testes de validação:

```bash
# Torna o script executável (apenas uma vez)
chmod +x scripts/test-health-jacoco.sh

# Opções disponíveis:
./scripts/test-health-jacoco.sh health   # Testa health checks
./scripts/test-health-jacoco.sh jacoco   # Executa testes unitários + cobertura
./scripts/test-health-jacoco.sh apis     # Testa APIs básicas
./scripts/test-health-jacoco.sh all      # Executa todos os testes
```

### Testes Manuais - Health Checks

```bash
# Verificar se aplicação está respondendo
curl http://localhost:8080/btg-orders/actuator/health

# Testar endpoints do Actuator
curl http://localhost:8080/btg-orders/actuator/health/liveness
curl http://localhost:8080/btg-orders/actuator/health/readiness
curl http://localhost:8080/btg-orders/actuator/metrics
curl http://localhost:8080/btg-orders/actuator/prometheus
```

### Testes Manuais - Cobertura de Código

```bash
# Executar todos os testes unitários
mvn clean test

# Gerar relatório JaCoCo
mvn jacoco:report

# Abrir relatório de cobertura
open target/site/jacoco/index.html
```

### Validação Completa (Opcional)

```bash
# Verificar build completo + testes + cobertura
mvn clean verify

# Resultado esperado:
# - 336 testes executados
# - 100% de cobertura
# - Build SUCCESS
```

### Como Testar as APIs de Negócio

1. **Certifique-se que a aplicação está rodando**:
   ```bash
   curl http://localhost:8080/btg-orders/actuator/health
   ```

2. **Publique uma mensagem de teste**:
   - Acesse: http://localhost:15672 (guest/guest)
   - Vá em **Queues → orders.queue → Publish message**
   - Cole o JSON do exemplo acima

3. **Teste as APIs**:
   ```bash
   # Valor total do pedido (substitua 1001 pelo ID do seu pedido)
   curl http://localhost:8080/btg-orders/api/v1/orders/1001/total
   
   # Quantidade de pedidos por cliente
   curl http://localhost:8080/btg-orders/api/v1/customers/1/orders/count
   
   # Lista paginada de pedidos
   curl "http://localhost:8080/btg-orders/api/v1/customers/1/orders?page=0&size=10"
   ```

4. **Ou use o Swagger UI**: http://localhost:8080/btg-orders/swagger-ui.html

---

| Categoria | Tecnologia | Versão |
|-----------|------------|--------|
| **Linguagem** | Java | 21 (LTS) |
| **Framework** | Spring Boot | 3.3.4 |
| **Banco de Dados** | PostgreSQL | 16 |
| **Mensageria** | RabbitMQ | 3.13 |
| **Build** | Maven | 3.9+ |
| **Containerização** | Docker + Compose | - |
| **Documentação** | OpenAPI 3.0 + Swagger | - |

---

## 🏗️ Arquitetura

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
│  │Resources│ │Providers│ │Configs  │   │
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

**Detalhes**: Ver [DOCUMENTACAO-TECNICA.md](DOCUMENTACAO-TECNICA.md)

---

## 🐳 Docker

### Opções de Execução

```bash
# Opção 1: Docker Compose (recomendado)
docker compose up -d

# Opção 2: Imagem do Docker Hub
docker pull freshiagoomes/btg-orders-service:latest

# Opção 3: Build local
mvn clean package -DskipTests
docker build -t btg-orders-service .
```

### Serviços Incluídos

- **PostgreSQL 16** (porta 5432)
- **RabbitMQ 3.13** (portas 5672, 15672)
- **BTG Orders Service** (porta 8080)

---

## 📊 Qualidade

### Testes
- **332 testes** automatizados
- **100% cobertura** de código
- Testes unitários + integração

### Executar Testes

```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

---

## 🔗 Links Importantes

| Recurso | URL |
|---------|-----|
| **Repositório GitHub** | https://github.com/iagoomes/btg-orders-challenge |
| **Docker Hub** | https://hub.docker.com/r/freshiagoomes/btg-orders-service |
| **Aplicação Local** | http://localhost:8080/btg-orders |
| **Swagger UI** | http://localhost:8080/btg-orders/swagger-ui.html |
| **RabbitMQ Management** | http://localhost:15672 |

---

## 📧 Contato

**Iago Gomes Antonio**  
📧 Email: iagoomes@outlook.com  
🐙 GitHub: [@iagoomes](https://github.com/iagoomes)  
🐳 Docker Hub: [freshiagoomes](https://hub.docker.com/u/freshiagoomes)

---

## 📄 Licença

Este projeto foi desenvolvido para o **Desafio Técnico BTG Pactual 2025**.

**Status**: ✅ **PROJETO CONCLUÍDO COM SUCESSO**
