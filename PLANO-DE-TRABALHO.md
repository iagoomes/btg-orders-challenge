# Plano de Trabalho - Desafio BTG Pactual

## Introdução e Estratégia de Desenvolvimento

A estratégia para o desenvolvimento do projeto será utilizar padrões de projeto e padrões de desenvolvimento que se baseiam em segurança e boas práticas, como deve ser em um domínio bancário.

Visando a reutilização de código, seguirei com desenvolvimento voltado a especificações, nesse caso o core será a OpenAPI Specification. Com ela conseguimos definir nossos recursos GET, POST, PUT, DELETE, e PATCH, se necessário. Dessa forma conseguimos disponibilizar a especificação para todos os stakeholders garantindo que a equipe de desenvolvimento e consumidores estejam alinhados com a necessidade do APP e dos seus recursos. Esta abordagem facilita o entendimento de outras áreas sobre a existência de contratos ou implementações que ofereçam recursos específicos, permitindo avaliar se é mais eficiente reutilizar os recursos da implementação atual ou desenvolver uma nova implementação em outras verticais. Além disso, ganhamos velocidade no desenvolvimento através do uso do plugin generator, que automatiza a criação de código baseado nas especificações.

Sobre o banco de dados, considerando que o cenário do projeto faz mais sentido utilizar um Banco de Dados Relacional devido à necessidade de JOINs para gerar e informar relatórios, e por ter conhecimento prévio em Oracle, optei pela utilização do PostgreSQL. A escolha se justifica pela similaridade entre PostgreSQL e Oracle em relação à linguagem Procedural, gerando mais conforto e produtividade no desenvolvimento.

Para o versionamento do APP, seguirei utilizando o GitHub e o Docker para montar meu ambiente de desenvolvimento de forma mais controlada e conteinerizada, facilitando a disponibilização e deploy, se necessário.

## Stack Tecnológica

- **Java 21** + Spring Boot 3.3
- **PostgreSQL 16** (banco relacional para relatórios)
- **RabbitMQ 3.13**
- **OpenAPI 3.0** + Generator Plugin
- **Docker** + **Docker Compose**
- **GitHub** para versionamento

## Atividades e Estimativas

### FASE 1: Setup e Configuração (4h)

| Task | Descrição | Estimativa |
| --- | --- | --- |
| T1.1 | Configuração projeto Spring Boot + dependências | 1h |
| T1.2 | Configuração OpenAPI e Generator Plugin | 1h |
| T1.3 | Docker Compose (PostgreSQL + RabbitMQ) | 1h |
| T1.4 | Estrutura inicial do projeto e GitHub | 1h |

### FASE 2: Modelagem e Design (3h)

| Task | Descrição | Estimativa |
| --- | --- | --- |
| T2.1 | Modelagem banco de dados (Cliente, Pedido, ItemPedido) | 1h |
| T2.2 | Definição contratos OpenAPI (endpoints REST) | 1h |
| T2.3 | Diagrama de arquitetura da solução | 1h |

### FASE 3: Implementação Core - Microsserviço (8h)

| Task | Descrição | Estimativa |
| --- | --- | --- |
| T3.1 | Entidades JPA (Pedido, Cliente, ItemPedido) | 2h |
| T3.2 | Repositories e configuração do banco | 1h |
| T3.3 | Consumer RabbitMQ para processar pedidos | 3h |
| T3.4 | Services de negócio (processamento e cálculos) | 2h |

### FASE 4: API REST - Consultas (6h)

| Task | Descrição | Estimativa |
| --- | --- | --- |
| T4.1 | Endpoint: Valor total do pedido | 1.5h |
| T4.2 | Endpoint: Quantidade de pedidos por cliente | 1.5h |
| T4.3 | Endpoint: Lista de pedidos por cliente | 2h |
| T4.4 | Tratamento de erros e validações | 1h |

### FASE 5: Testes (4h)

| Task | Descrição | Estimativa |
| --- | --- | --- |
| T5.1 | Testes unitários (Services) | 2h |
| T5.2 | Testes de integração (API REST) | 1h |
| T5.3 | Testes funcionais e evidências | 1h |

### FASE 6: Containerização e Deploy (4h)

| Task | Descrição | Estimativa |
| --- | --- | --- |
| T6.1 | Dockerfile para aplicação Spring Boot | 1h |
| T6.2 | Docker Compose completo (app + infra) | 2h |
| T6.3 | Publicação imagem Docker Hub | 1h |

### FASE 7: Documentação e Relatório (3h)

| Task | Descrição | Estimativa |
| --- | --- | --- |
| T7.1 | README com instruções de execução | 1h |
| T7.2 | Relatório técnico completo (conforme template) | 1.5h |
| T7.3 | Diagramas (implantação, infra cloud) | 0.5h |

## Cronograma (7 dias)

| Dia | Fases | Horas | Atividades | Entregáveis |
| --- | --- | --- | --- | --- |
| **Dia 1** | 1-2 | 7h | Setup + Modelagem | Plano de Trabalho (entrega em 24h) |
| **Dia 2-3** | 3 | 8h | Implementação Core (Microsserviço) | Consumer RabbitMQ funcionando |
| **Dia 4** | 4 | 6h | API REST | Endpoints de consulta |
| **Dia 5** | 5 | 4h | Testes | Testes automatizados |
| **Dia 6** | 6 | 4h | Docker + Deploy | Aplicação containerizada |
| **Dia 7** | 7 | 3h | Documentação | Relatório final + GitHub |

**Total**: 32 horas
