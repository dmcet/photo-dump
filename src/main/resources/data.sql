-- First, make sure the table exists
CREATE TABLE IF NOT EXISTS images (
    id SERIAL PRIMARY KEY,
    data BINARY LARGE OBJECT,
    name VARCHAR(255)
);