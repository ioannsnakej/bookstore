from flask import Flask, jsonify, request
from db import get_connection

app = Flask(__name__)

@app.route('/')
def home():
    return "Hello!", 200

@app.route('/books', methods=['GET'])
def get_books():
    conn = get_connection()
    cur = conn.cursor()
    cur.execute('SELECT id, title, author, price FROM books;')
    rows = cur.fetchall()
    cur.close()
    conn.close()

    books = [
        {
            'id': r[0],
            'title': r[1],
            'author': r[2],
            'price': float(r[3])
        }
        for r in rows
    ]
    return jsonify(books)

@app.route('/books', methods=['POST'])
def add_book():
    data = request.json
    conn = get_connection()
    cur = conn.cursor()
    cur.execute(
        'INSERT INTO books (title, author, price) VALUES (%s, %s, %s) RETURNING id;',
        (data['title'], data['author'], data['price'])
    )
    book_id = cur.fetchone()[0]
    conn.commit()
    cur.close()
    conn.close()
    return jsonify(
        {
            'id': book_id
        }
    ), 201

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
