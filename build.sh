#!/bin/bash
echo "========================================="
echo "СБОРКА JAVA БЭКЕНДА"
echo "========================================="
cd java-backend
mvn clean package
cd ..
echo "✅ Сборка завершена!"
echo "Запустите: docker-compose up --build"
