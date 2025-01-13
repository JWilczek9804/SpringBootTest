CREATE TABLE customer (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    age INT NOT NULL,
    gender TEXT NOT NULL
)