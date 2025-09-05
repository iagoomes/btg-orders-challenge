# 📋 Plano de Trabalho - Desafio BTG Pactual

**Candidato**: Iago Gomes Antonio  
**Data**: 03/09/2025  
**Prazo**: 7 dias corridos  

---

## 🎯 Estratégia de Desenvolvimento

**Abordagem Contract-First** usando OpenAPI 3.0 para:
- Definir contratos de API antes da implementação
- Gerar código automaticamente (controllers, interfaces)
- Garantir documentação sempre atualizada
- Facilitar desenvolvimento paralelo

**Arquitetura Clean** para:
- Separação clara de responsabilidades
- Facilitar testes e manutenção
- Aplicar padrões bancários de segurança

---

## 🛠️ Stack Tecnológica Escolhida

| Componente | Tecnologia | Justificativa |
|------------|------------|---------------|
| **Linguagem** | Java 21 + Spring Boot 3.3 | Estabilidade e produtividade |
| **Banco** | PostgreSQL 16 | Necessidade de JOINs para relatórios |
| **Mensageria** | RabbitMQ 3.13 | Simplicidade e DLQ nativo |
| **Build** | Maven + OpenAPI Generator | Automação de código |
| **Deploy** | Docker + Docker Compose | Ambiente controlado |

---

## 📅 Cronograma e Atividades

### FASE 1: Setup e Configuração (4h - Dia 1)

| Task | Descrição | Estimativa |
|------|-----------|------------|
| T1.1 | Setup Spring Boot + dependências | 1h |
| T1.2 | Configuração OpenAPI + Generator | 1h |
| T1.3 | Docker Compose (PostgreSQL + RabbitMQ) | 1h |
| T1.4 | Estrutura inicial do projeto | 1h |

### FASE 2: Modelagem (3h - Dia 1)

| Task | Descrição | Estimativa |
|------|-----------|------------|
| T2.1 | Modelagem banco (Cliente, Pedido, Item) | 1h |
| T2.2 | Contratos OpenAPI (3 endpoints) | 1h |
| T2.3 | Arquitetura da solução | 1h |

### FASE 3: Core - Microsserviço (8h - Dias 2-3)

| Task | Descrição | Estimativa |
|------|-----------|------------|
| T3.1 | Entidades JPA | 2h |
| T3.2 | Repositories + configuração DB | 1h |
| T3.3 | Consumer RabbitMQ | 3h |
| T3.4 | Services de negócio | 2h |

### FASE 4: APIs REST (6h - Dia 4)

| Task | Descrição | Estimativa |
|------|-----------|------------|
| T4.1 | Endpoint: Valor total do pedido | 1.5h |
| T4.2 | Endpoint: Quantidade pedidos por cliente | 1.5h |
| T4.3 | Endpoint: Lista pedidos paginada | 2h |
| T4.4 | Validações e tratamento de erros | 1h |

### FASE 5: Testes (4h - Dia 5)

| Task | Descrição | Estimativa |
|------|-----------|------------|
| T5.1 | Testes unitários | 2h |
| T5.2 | Testes de integração | 1h |
| T5.3 | Evidências funcionais | 1h |

### FASE 6: Containerização (4h - Dia 6)

| Task | Descrição | Estimativa |
|------|-----------|------------|
| T6.1 | Dockerfile aplicação | 1h |
| T6.2 | Docker Compose completo | 2h |
| T6.3 | Publicação Docker Hub | 1h |

### FASE 7: Documentação (3h - Dia 7)

| Task | Descrição | Estimativa |
|------|-----------|------------|
| T7.1 | README com instruções | 1h |
| T7.2 | Relatório técnico | 1.5h |
| T7.3 | Revisão final | 0.5h |

---

## 📊 Resumo do Cronograma

| Dia | Fases | Horas | Entregáveis |
|-----|-------|-------|-------------|
| **1** | Setup + Modelagem | 7h | Plano + Arquitetura |
| **2-3** | Implementação Core | 8h | Consumer RabbitMQ |
| **4** | APIs REST | 6h | Endpoints funcionando |
| **5** | Testes | 4h | Cobertura completa |
| **6** | Docker | 4h | Aplicação containerizada |
| **7** | Documentação | 3h | **Entrega final** |

**Total Estimado**: 32 horas

---

## 🎯 Critérios de Aceite

### Funcionalidades Obrigatórias
- ✅ Consumer RabbitMQ processando mensagens no formato especificado
- ✅ 3 endpoints REST funcionando corretamente
- ✅ Banco PostgreSQL com modelo relacional adequado
- ✅ Aplicação containerizada e rodando

### Qualidade
- ✅ Testes automatizados com boa cobertura
- ✅ Documentação clara e completa
- ✅ Código seguindo padrões de qualidade

### Entrega
- ✅ Repositório GitHub público
- ✅ Imagem Docker Hub
- ✅ Instruções de execução funcionais
- ✅ Relatório técnico completo

---

**Status**: ✅ **PLANO APROVADO E EM EXECUÇÃO**
