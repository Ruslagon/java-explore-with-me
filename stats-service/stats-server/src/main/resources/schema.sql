drop table IF EXISTS endpoint CASCADE;

CREATE TABLE IF NOT EXISTS endpoint
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    timestamp TIMESTAMP WITHOUT TIME ZONE,
    app varchar(150) NOT NULL,
    uri varchar(150) NOT NULL,
    ip varchar(150) NOT NULL
);