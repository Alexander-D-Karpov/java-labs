CREATE TABLE IF NOT EXISTS users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS  tickets (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         coordinates_x INT NOT NULL,
                         coordinates_y INT NOT NULL,
                         creation_date TIMESTAMP NOT NULL,
                         price BIGINT,
                         discount BIGINT,
                         ticket_type VARCHAR(20),
                         event_id INT,
                         user_id INT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS  events (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        tickets_count BIGINT NOT NULL,
                        description TEXT,
                        event_type VARCHAR(20)
);