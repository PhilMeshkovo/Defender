# Defender
Антиспам телеграм бот

# Postgres
Создаем таблицу в вашей базе
```
CREATE TABLE spam_keywords (
    id SERIAL PRIMARY KEY,
    keyword TEXT UNIQUE
);
```
и заполняем ее значениями слов, определяющих спам сообщения

# Spring application
Создаем application.properties в resources, пример:
```
telegram.bot.username=bot_nick
telegram.bot.token=bot_token_from_father_bot

logging.level.root=INFO
logging.level.com.phil.antispam.defender=DEBUG

spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/database_name}
spring.datasource.username=${DB_USERNAME:user}
spring.datasource.password=${DB_PASSWORD:user_password}
spring.datasource.hikari.pool-name=HikariCP
spring.datasource.validationQuery=SELECT 1
```

# Docker
Запускаем контейнер с указанием конфигов подключения к БД через переменные в командной строке, пример:
```
sudo docker run -d   -e DB_URL="jdbc:postgresql://192.168.31.6:5432/database_name"   -e DB_USERNAME="user"   -e DB_PASSWORD="user_password"   -p 8081:8080   container_name
```

