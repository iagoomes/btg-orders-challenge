# 🏦 BTG Orders Service - Desafio Técnico

**Candidato**: Iago Gomes Antonio  
**Email**: iagoomes@outlook.com  
**GitHub**: https://github.com/iagoomes/btg-orders-challenge  
**Docker Hub**: https://hub.docker.com/r/freshiagoomes/btg-orders-service  

[![Java 21](https://img.shields.io/badge/java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.5](https://img.shields.io/badge/spring--boot-3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/docker-ready-blue)](https://hub.docker.com/r/freshiagoomes/btg-orders-service)
[![Coverage](https://img.shields.io/badge/coverage-100%25-success)](./target/site/jacoco)

---

## 📋 Desafio BTG Pactual - Solução Completa

Microsserviço para processamento de pedidos com mensageria RabbitMQ e APIs REST.

### 📚 Documentos

| Documento | Link |
|-----------|------|
| **Plano de Trabalho** | [📋 PLANO-DE-TRABALHO.md](./PLANO-DE-TRABALHO.md) |
| **Relatório Técnico** | [📊 RELATORIO-TECNICO.md](./RELATORIO-TECNICO.md) |
| **Documentação Técnica** | [📚 DOCUMENTACAO-TECNICA.md](DOCUMENTACAO-TECNICA.md) |

---

## 🚀 Execução Rápida

### Opção 1: Docker Compose (Recomendado)
```bash
# 1. Clonar repositório
git clone https://github.com/iagoomes/btg-orders-challenge.git
cd orders-service

# 2. Executar ambiente completo
docker compose up -d

# 3. Verificar status
curl http://localhost:8080/btg-orders/actuator/health
```

### Opção 2: Imagem Docker Hub (Deploy Direto)
```bash
# 1. Baixar imagem publicada
docker pull freshiagoomes/btg-orders-service:latest

# 2. Executar dependências (PostgreSQL + RabbitMQ)
docker run -d --name postgres-btg \
  -e POSTGRES_DB=orders_db \
  -e POSTGRES_USER=orders_user \
  -e POSTGRES_PASSWORD=orders_pass \
  -p 5432:5432 \
  postgres:16-alpine

docker run -d --name rabbitmq-btg \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3.13-management-alpine

# 3. Executar aplicação BTG
docker run -d --name btg-orders-app \
  -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e RABBITMQ_HOST=host.docker.internal \
  freshiagoomes/btg-orders-service:latest

# 4. Verificar status
curl http://localhost:8080/btg-orders/actuator/health
```

### Opção 3: Docker Compose + Imagem Hub
```bash
# Usar imagem do registry em vez de build local
docker compose -f docker-compose.yml pull orders-service
docker compose up -d
```

### Acesso às Interfaces

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| **Swagger UI** | http://localhost:8080/btg-orders/swagger-ui.html | - |
| **RabbitMQ Management** | http://localhost:15672 | guest/guest |
| **Health Check** | http://localhost:8080/btg-orders/actuator/health | - |

---

## ✅ Requisitos BTG Atendidos

| Item | Requisito BTG | Status | Evidência |
|------|---------------|---------|-----------|
| 1 | Plano de trabalho | ✅ | [PLANO-DE-TRABALHO.md](./PLANO-DE-TRABALHO.md) |
| 2 | Aplicação Java | ✅ | Spring Boot 3.5 + Java 21 |
| 3 | Base PostgreSQL | ✅ | JPA + Hibernate |
| 4 | Microsserviço RabbitMQ | ✅ | Consumer funcionando |
| 5 | API REST (3 endpoints) | ✅ | Swagger disponível |
| 6 | Relatório técnico | ✅ | [RELATORIO-TECNICO.md](./RELATORIO-TECNICO.md) |

### APIs Implementadas (Conforme BTG)

| Endpoint | Descrição BTG | Status |
|----------|---------------|---------|
| `GET /api/v1/orders/{id}/total` | Valor total do pedido | ✅ |
| `GET /api/v1/customers/{id}/orders/count` | Quantidade de pedidos por cliente | ✅ |
| `GET /api/v1/customers/{id}/orders` | Lista de pedidos por cliente | ✅ |

### Formato Mensagem RabbitMQ

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
> **Nota**: Corrigido estrutura inconsistente do exemplo BTG original

---

## 🧪 Teste da Aplicação

### 1. Publicar Mensagem RabbitMQ
1. Acesse http://localhost:15672 (guest/guest)
2. Queues → orders.queue → Publish message
3. Cole JSON acima
4. Publish message

### 2. Testar APIs
```bash
# Valor total do pedido
curl http://localhost:8080/btg-orders/api/v1/orders/1001/total

# Quantidade de pedidos por cliente  
curl http://localhost:8080/btg-orders/api/v1/customers/1/orders/count

# Lista de pedidos por cliente
curl "http://localhost:8080/btg-orders/api/v1/customers/1/orders?page=0&size=10"
```

---

## 🛠️ Stack Tecnológica

| Categoria | Tecnologia | Versão |
|-----------|------------|--------|
| **Linguagem** | Java | 21 (LTS) |
| **Framework** | Spring Boot | 3.5.5 |
| **Banco** | PostgreSQL | 16 |
| **Mensageria** | RabbitMQ | 3.13 |
| **Build** | Maven | 3.9+ |
| **Container** | Docker + Compose | - |
| **Documentação** | OpenAPI 3.0 + Swagger | - |

---

## 📊 Qualidade

- **336 testes** automatizados (100% cobertura)
- **Clean Architecture** (4 camadas)
- **Docker** ambiente completo
- **OpenAPI-First** desenvolvimento

### Executar Testes (Opcional)
```bash
# Cobertura completa
mvn clean verify

# Relatório JaCoCo
open target/site/jacoco/index.html
```

---

## 🔗 Links BTG

| Recurso | URL |
|---------|-----|
| **GitHub** | https://github.com/iagoomes/btg-orders-challenge |
| **Docker Hub** | https://hub.docker.com/r/freshiagoomes/btg-orders-service |
| **Swagger UI** | http://localhost:8080/btg-orders/swagger-ui.html |

---

## 📧 Contato

**Iago Gomes Antonio**  
📧 iagoomes@outlook.com  
🐙 [@iagoomes](https://github.com/iagoomes)  
🐳 [freshiagoomes](https://hub.docker.com/u/freshiagoomes)

---

**Status**: ✅ **DESAFIO BTG CONCLUÍDO**
