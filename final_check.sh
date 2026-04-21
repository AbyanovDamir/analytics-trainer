#!/bin/bash
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo "========================================="
echo "ФИНАЛЬНАЯ ПРОВЕРКА ПЕРЕД СДАЧЕЙ"
echo "========================================="

docker ps | grep -q trainer-postgres && echo -e "${GREEN}✅ PostgreSQL запущен${NC}" || echo -e "${RED}❌ PostgreSQL не запущен${NC}"
docker ps | grep -q trainer-backend && echo -e "${GREEN}✅ Backend запущен${NC}" || echo -e "${RED}❌ Backend не запущен${NC}"
docker ps | grep -q trainer-checker && echo -e "${GREEN}✅ Checker запущен${NC}" || echo -e "${RED}❌ Checker не запущен${NC}"
curl -s http://localhost:8081/health > /dev/null && echo -e "${GREEN}✅ Go Checker OK${NC}" || echo -e "${RED}❌ Go Checker не отвечает${NC}"
curl -s http://localhost:8080/trainings > /dev/null && echo -e "${GREEN}✅ Backend OK${NC}" || echo -e "${RED}❌ Backend не отвечает${NC}"
TASKS=$(docker exec trainer-postgres psql -U admin -d trainer -t -c "SELECT COUNT(*) FROM tasks" | xargs)
echo -e "${GREEN}📊 Заданий в БД: $TASKS (ожидается 10)${NC}"
echo "========================================="
