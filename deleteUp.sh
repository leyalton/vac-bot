#!/bin/bash

# Используем цвета для визуального выделения текста
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Функция для вывода заголовков с отступами
print_header() {
  printf "\n${YELLOW}====> %s <====${NC}\n" "$1"
}

# Остановить и удалить все контейнеры
print_header "Остановка и удаление контейнеров"
echo -e "${YELLOW}Останавливаем контейнеры...${NC}"
docker compose down

# Удалить Docker контейнер
# В docker compose down уже удаляется контейнер, поэтому этот шаг можно пропустить
# echo -e "${YELLOW}Удаляем контейнер${NC}"

# Удалить Docker образ
print_header "Удаление Docker образа"
echo -e "${YELLOW}Удаляем Docker образ...${NC}"
docker rmi vacancy-bot-parser-vacancy:latest

# Запуск контейнеров
print_header "Запуск контейнеров"
echo -e "${YELLOW}Запускаем контейнеры...${NC}"
docker compose up -d

echo -e "\n${GREEN}Операция завершена.${NC}"