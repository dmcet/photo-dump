CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(500) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS images
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255)                                    NOT NULL,
    owner_id INTEGER REFERENCES users (id) ON DELETE CASCADE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_images_owner_id ON images (owner_id)