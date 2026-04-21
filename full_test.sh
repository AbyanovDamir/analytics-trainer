#!/bin/bash
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'
PASSED=0
FAILED=0

test_endpoint() {
    local name=$1
    local url=$2
    echo -n "Тест: $name ... "
    if curl -s -f "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ ПРОЙДЕН${NC}"
        ((PASSED++))
    else
        echo -e "${RED}✗ ПРОВАЛЕН${NC}"
        ((FAILED++))
    fi
}

echo "========================================="
echo "ПОЛНОЕ ТЕСТИРОВАНИЕ"
echo "========================================="
test_endpoint "Health check Go" "http://localhost:8081/health"
test_endpoint "Список заданий" "http://localhost:8080/trainings"
test_endpoint "Задание по ID" "http://localhost:8080/trainings/1"
echo -e "\n${GREEN}Пройдено: $PASSED${NC}"
echo -e "${RED}Провалено: $FAILED${NC}"
