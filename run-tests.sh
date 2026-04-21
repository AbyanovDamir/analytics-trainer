#!/bin/bash
GREEN='\033[0;32m'
NC='\033[0m'
echo "========================================="
echo "БЫСТРОЕ ТЕСТИРОВАНИЕ API"
echo "========================================="
echo -e "\n${GREEN}1. Проверка Go чекера:${NC}"
curl -s http://localhost:8081/health | jq .
echo -e "\n${GREEN}2. Проверка списка заданий:${NC}"
curl -s http://localhost:8080/trainings | jq '.[] | {id, type, title}'
echo -e "\n${GREEN}3. Регистрация пользователя:${NC}"
curl -s -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"123456","fullName":"Test User"}' | jq .
