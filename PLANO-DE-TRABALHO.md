# 📋 Plano de Trabalho - Desafio BTG Pactual

**Candidato**: Iago Gomes Antonio  
**Data**: 03/09/2025  
**Prazo**: 7 dias corridos  

---

## 🏛️ Introdução e Estratégia de Desenvolvimento

A estratégia para o desenvolvimento do projeto será utilizar **padrões de projeto e padrões de desenvolvimento que se baseiam em segurança e boas práticas**, como deve ser em um **domínio bancário**.

### Desenvolvimento Orientado a Especificações (OpenAPI-First)

Visando a **reutilização de código**, seguirei com desenvolvimento voltado a especificações, nesse caso o core será a **OpenAPI Specification**. Com ela conseguimos definir nossos recursos GET, POST, PUT, DELETE, e PATCH, se necessário. 

**Benefícios desta abordagem**:
- **Alinhamento de stakeholders**: Garantir que equipe de desenvolvimento e consumidores estejam alinhados com a necessidade do APP e dos seus recursos
- **Facilita reutilização**: Permitir avaliar se é mais eficiente reutilizar os recursos da implementação atual ou desenvolver uma nova implementação em outras verticais
- **Velocidade no desenvolvimento**: Ganhar velocidade através do uso do plugin generator, que automatiza a criação de código baseado nas especificações
- **Contratos bem definidos**: Disponibilizar especificação para todos os stakeholders

### Decisão Arquitetural - Banco de Dados

Sobre o banco de dados, considerando que o cenário do projeto faz mais sentido utilizar um **Banco de Dados Relacional** devido à:
- **Necessidade de JOINs** para gerar e informar relatórios
- **Consistência ACID** essencial para domínio financeiro
- **Conhecimento prévio em Oracle**: Optei pela utilização do **PostgreSQL** pela similaridade entre PostgreSQL e Oracle em relação à linguagem Procedural, gerando mais conforto e produtividade no desenvolvimento

### Estratégia de Versionamento e Deploy

Para o versionamento do APP, seguirei utilizando:
- **GitHub** para controle de versão e colaboração
- **Docker** para montar ambiente de desenvolvimento de forma mais controlada e containerizada
- **Facilitar disponibilização e deploy** quando necessário

---

## 🎯 Estratégia de Desenvolvimento

**Abordagem Contract-First** usando OpenAPI 3.0 para:
- Definir contratos de API antes da implementação
- Gerar código automaticamente
- Garantir documentação sempre atualizada

**Arquitetura Clean** para:
- Separação clara de responsabilidades
- Facilitar testes e manutenção
- Aplicar padrões enterprise

---

## 🛠️ Stack Tecnológica Escolhida

| Componente | Tecnologia | Justificativa |
|------------|------------|---------------|
| **Linguagem** | Java 21 + Spring Boot 3.5 | Estabilidade e produtividade enterprise |
| **Banco** | PostgreSQL 16 | Similaridade com Oracle + JOINs necessários |
| **Mensageria** | RabbitMQ 3.13 | DLQ nativo + simplicidade operacional |
| **Build** | Maven + OpenAPI Generator | Automação de código + reutilização |
| **Deploy** | Docker + Docker Compose | Ambiente controlado + facilita deploy |

---

## 📅 Cronograma BTG

### FASE 1: Setup (4h - Dia 1)
- T1.1: Spring Boot + dependências (1h)
- T1.2: OpenAPI + Generator (1h)
- T1.3: Docker Compose (PostgreSQL + RabbitMQ) (1h)
- T1.4: Estrutura inicial (1h)

### FASE 2: Modelagem (3h - Dia 1)
- T2.1: Modelagem banco (Cliente, Pedido, Item) (1h)
- T2.2: Contratos OpenAPI (3 endpoints BTG) (1h)
- T2.3: Arquitetura da solução (1h)

### FASE 3: Microsserviço RabbitMQ (8h - Dias 2-3)
- T3.1: Entidades JPA (2h)
- T3.2: Repositories + configuração DB (1h)
- T3.3: Consumer RabbitMQ (3h)
- T3.4: Services de negócio (2h)

### FASE 4: APIs REST BTG (6h - Dia 4)
- T4.1: Endpoint: Valor total do pedido (1.5h)
- T4.2: Endpoint: Quantidade pedidos por cliente (1.5h)
- T4.3: Endpoint: Lista pedidos por cliente (2h)
- T4.4: Validações e tratamento de erros (1h)

### FASE 5: Testes (4h - Dia 5)
- T5.1: Testes unitários (2h)
- T5.2: Testes de integração (1h)
- T5.3: Evidências funcionais (1h)

### FASE 6: Docker (3h - Dia 6)
- T6.1: Dockerfile aplicação (1h)
- T6.2: Docker Compose completo (1h)
- T6.3: Publicação Docker Hub (1h)

### FASE 7: Documentação (3h - Dia 7)
- T7.1: README com instruções (1h)
- T7.2: Relatório técnico BTG (1.5h)
- T7.3: Revisão final (0.5h)

---

## 📊 Resumo

| Dia | Fases | Horas | Entregáveis |
|-----|-------|-------|-------------|
| **1** | Setup + Modelagem | 7h | Plano + Arquitetura |
| **2-3** | Microsserviço RabbitMQ | 8h | Consumer funcionando |
| **4** | APIs REST BTG | 6h | 3 endpoints funcionando |
| **5** | Testes | 4h | Cobertura completa |
| **6** | Docker | 3h | Aplicação containerizada |
| **7** | Documentação | 3h | **Entrega final** |

**Total Estimado**: 31 horas

---

## 🎯 Critérios de Aceite BTG

### Funcionalidades Obrigatórias
- ✅ Consumer RabbitMQ processando mensagens no formato BTG
- ✅ 3 endpoints REST conforme especificação BTG
- ✅ Banco PostgreSQL com modelo relacional
- ✅ Aplicação containerizada

### Qualidade
- ✅ Testes automatizados com boa cobertura
- ✅ Documentação clara e completa
- ✅ Código seguindo padrões de qualidade

### Entrega BTG
- ✅ Repositório GitHub público
- ✅ Imagem Docker Hub
- ✅ Instruções de execução funcionais
- ✅ Relatório técnico conforme especificação BTG

---

**Status**: ✅ **PLANO EXECUTADO COM SUCESSO**
