<h2>Запуск:</h2>

    docker compose up -v
<h2>Примеры запросов:</h2>

    curl -X POST http://localhost:80/books \
    -H "Content-Type: application/json" \
    -d '{"title": "Дежавю", "author": "Kizaru", "price": 743}'
    
    curl 127.0.0.1:80/books | jq .
    
    curl 127.0.0.1/books
