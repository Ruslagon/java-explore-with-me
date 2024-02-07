drop table IF EXISTS categories, locations, users, events, participations, compilations, event_compilations  CASCADE;

CREATE TABLE IF NOT EXISTS categories
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lat float NOT NULL,
    lon float NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(250) NOT NULL,
    email varchar(254) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation varchar(2000) NOT NULL,
    category_id BIGINT,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    description varchar(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    initiator_id BIGINT,
    location_id BIGINT,
    paid BOOLEAN NOT NULL,
    moderation BOOLEAN NOT NULL,
    state varchar(50) NOT NULL,
    title varchar(120) NOT NULL,
    participant_limit INT NOT NULL,
    CONSTRAINT fk_event_to_category FOREIGN KEY(category_id) REFERENCES categories(id),
    CONSTRAINT fk_event_to_user FOREIGN KEY(initiator_id) REFERENCES users(id),
    CONSTRAINT fk_event_to_location_id FOREIGN KEY(location_id) REFERENCES locations(id)
);

CREATE TABLE IF NOT EXISTS participations
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE,
    status varchar(50) NOT NULL,
    requester_id BIGINT,
    event_id BIGINT,
    CONSTRAINT fk_participations_to_users FOREIGN KEY(requester_id) REFERENCES users(id),
    CONSTRAINT fk_participations_to_events FOREIGN KEY(event_id) REFERENCES events(id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title varchar(50) NOT NULL,
    pinned BOOLEAN NOT NULL
);

CREATE TABLE event_compilations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id BIGINT,
    compilations_id BIGINT,
    CONSTRAINT fk_part_to_events FOREIGN KEY(event_id) REFERENCES events(id),
    CONSTRAINT fk_part_to_compilations FOREIGN KEY(compilations_id) REFERENCES compilations(id)
--    FOREIGN KEY (event_id) REFERENCES events(id),
--    FOREIGN KEY (compilations_id) REFERENCES compilations(id)
);