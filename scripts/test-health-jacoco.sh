#!/bin/bash

# Script para testar Health Check do Actuator e JaCoCo - EXECUÇÃO MANUAL

echo "=== Script de Testes BTG Orders Service ==="
echo "IMPORTANTE: Este script executa testes opcionais e NÃO faz parte do deploy automático"
echo ""

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para verificar se a aplicação está rodando
check_app_running() {
    echo -e "${BLUE}Verificando se a aplicação está rodando...${NC}"
    if curl -f -s http://localhost:8080/btg-orders/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Aplicação está rodando${NC}"
        return 0
    else
        echo -e "${RED}✗ Aplicação não está rodando${NC}"
        echo -e "${YELLOW}Execute: docker-compose up -d${NC}"
        return 1
    fi
}

# Função para testar endpoints do Actuator
test_actuator_endpoints() {
    echo -e "${BLUE}Testando endpoints do Actuator...${NC}"

    endpoints=(
        "health"
        "health/liveness"
        "health/readiness"
        "info"
        "metrics"
        "prometheus"
        "env"
    )

    for endpoint in "${endpoints[@]}"; do
        echo -n "Testing /actuator/$endpoint ... "
        if curl -f -s "http://localhost:8080/btg-orders/actuator/$endpoint" > /dev/null; then
            echo -e "${GREEN}✓${NC}"
        else
            echo -e "${RED}✗${NC}"
        fi
    done
}

# Função para executar testes e gerar relatório JaCoCo
run_jacoco_tests() {
    echo -e "${BLUE}Executando testes e gerando relatório JaCoCo...${NC}"

    # Limpar target anterior
    echo "Limpando build anterior..."
    mvn clean

    # Executar testes com JaCoCo
    echo "Executando testes..."
    mvn test

    # Gerar relatório
    echo "Gerando relatório JaCoCo..."
    mvn jacoco:report

    # Verificar cobertura
    echo "Verificando cobertura mínima..."
    mvn verify

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Cobertura de testes atende aos critérios mínimos${NC}"
    else
        echo -e "${YELLOW}⚠ Cobertura de testes abaixo do mínimo esperado${NC}"
    fi

    # Mostrar localização do relatório
    echo -e "${BLUE}Relatório JaCoCo disponível em:${NC}"
    echo "target/site/jacoco/index.html"
}

# Função para testar APIs REST
test_rest_apis() {
    echo -e "${BLUE}Testando APIs REST...${NC}"

    # Primeiro verificar se a aplicação está respondendo
    if ! check_app_running; then
        return 1
    fi

    echo "Testando endpoints principais..."

    # Teste básico de health
    echo -n "GET /actuator/health ... "
    if curl -f -s http://localhost:8080/btg-orders/actuator/health > /dev/null; then
        echo -e "${GREEN}✓${NC}"
    else
        echo -e "${RED}✗${NC}"
    fi

    # Teste de swagger
    echo -n "GET /swagger-ui.html ... "
    if curl -f -s http://localhost:8080/btg-orders/swagger-ui.html > /dev/null; then
        echo -e "${GREEN}✓${NC}"
    else
        echo -e "${RED}✗${NC}"
    fi

    echo ""
    echo -e "${BLUE}Para testar as APIs de negócio, publique uma mensagem no RabbitMQ primeiro:${NC}"
    echo "1. Acesse: http://localhost:15672 (guest/guest)"
    echo "2. Vá em Queues → orders.queue → Publish message"
    echo "3. Use o JSON de exemplo do README.md"
    echo ""
}

# Menu principal
case "${1:-help}" in
    "health")
        echo -e "${YELLOW}Testando aplicação e health checks...${NC}"
        check_app_running && test_actuator_endpoints
        ;;
    "jacoco")
        echo -e "${YELLOW}Executando testes JaCoCo...${NC}"
        run_jacoco_tests
        ;;
    "apis")
        echo -e "${YELLOW}Testando APIs REST...${NC}"
        test_rest_apis
        ;;
    "all")
        echo -e "${YELLOW}Executando todos os testes...${NC}"
        run_jacoco_tests
        echo ""
        check_app_running && test_actuator_endpoints
        ;;
    *)
        echo "Uso: $0 [health|jacoco|apis|all]"
        echo ""
        echo "health - Testa aplicação rodando + health checks"
        echo "jacoco - Executa testes unitários e gera relatório"
        echo "apis   - Testa APIs REST básicas"
        echo "all    - Executa todos os testes"
        echo ""
        echo "IMPORTANTE: Estes testes são opcionais e não fazem parte do deploy."
        ;;
esac
