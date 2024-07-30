FROM maven:3.8.7-openjdk-18-slim

# Создаем рабочую директорию
RUN mkdir vacansy-bot

WORKDIR vacansy-bot

# Копируем исходный код вашего проекта в контейнер
COPY . .

# Выполняем сборку Maven
RUN mvn install

# Указываем команду запуска вашего приложения
CMD ["java", "-jar", "target/main.jar"]