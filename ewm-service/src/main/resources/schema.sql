CREATE TABLE IF NOT EXISTS users
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_main_users PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS categories
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(250) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS compilations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY,
    title VARCHAR(250) NOT NULL,
    pinned bool,
    CONSTRAINT pk_compilations PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS requests
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    status INT NOT NULL,
    event BIGINT NOT NULL,
    requester BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT pk_requests PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS events
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    annotation VARCHAR(2000) NOT NULL,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(7000),
    state INT NOT NULL,
    confirmed_requests BIGINT NOT NULL DEFAULT 0,
    lat BIGINT NOT NULL,
    lon BIGINT NOT NULL,
    paid bool,
    participant_limit INT,
    request_moderation bool,
    is_available bool,
    views BIGINT NOT NULL DEFAULT 0,
    initiator BIGINT NOT NULL,
    category BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    published_on TIMESTAMP WITHOUT TIME ZONE,
    event_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_events PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS compilations_events_joins
(
    event_id BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    CONSTRAINT pk_joins PRIMARY KEY (event_id, compilation_id)
    );

-- создание внешних ключей

ALTER TABLE events DROP CONSTRAINT IF EXISTS fk_events_category;
ALTER TABLE events ADD CONSTRAINT fk_events_category FOREIGN KEY (category) REFERENCES categories (id) ON DELETE CASCADE;

ALTER TABLE events DROP CONSTRAINT IF EXISTS fk_events_initiator;
ALTER TABLE events ADD CONSTRAINT fk_events_initiator FOREIGN KEY (initiator) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE compilations_events_joins DROP CONSTRAINT IF EXISTS fk_compilations_events_event_id;
ALTER TABLE compilations_events_joins ADD CONSTRAINT fk_compilations_events_event_id FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE;

ALTER TABLE compilations_events_joins DROP CONSTRAINT IF EXISTS fk_compilations_events_compilation_id;
ALTER TABLE compilations_events_joins ADD CONSTRAINT fk_compilations_events_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE;

ALTER TABLE requests DROP CONSTRAINT IF EXISTS fk_requests_category;
ALTER TABLE requests ADD CONSTRAINT fk_requests_category FOREIGN KEY (event) REFERENCES events (id) ON DELETE CASCADE;

ALTER TABLE requests DROP CONSTRAINT IF EXISTS fk_requests_initiator;
ALTER TABLE requests ADD CONSTRAINT fk_requests_initiator FOREIGN KEY (requester) REFERENCES users (id) ON DELETE CASCADE;
