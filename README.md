# Defender
Антиспам телеграм бот

# Docker
Запускаем контейнер с указанием конфигов подключения к БД через переменные в командной строке, пример -
```
sudo docker run -d   -e DB_URL="jdbc:postgresql://192.168.31.6:5432/defender"   -e DB_USERNAME="user"   -e DB_PASSWORD="user_password"   -p 8081:8080   defender
```

