-- создаём таблицу users (с пользователями):
CREATE TABLE IF NOT EXISTS users(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name varchar(250) NOT NULL,
    email varchar(254) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

-- создаём таблицу categories (с категориями событий):
CREATE TABLE IF NOT EXISTS categories(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name varchar(50) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (category_id),
    CONSTRAINT uq_category_name UNIQUE (name)
);

-- создаём таблицу events (с событиями):
CREATE TABLE IF NOT EXISTS events(
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    annotation varchar(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests integer,
    creation_date timestamp,
    description varchar(7000) NOT NULL,
    event_date timestamp,
    user_id BIGINT NOT NULL,
    location_lat real,
    location_lon real,
    paid boolean NOT NULL,
    participant_limit integer NOT NULL,
    published_date timestamp,
    request_moderation boolean NOT NULL,
    event_status varchar(20) NOT NULL,
    title varchar(120) NOT NULL,
    views integer,
    available boolean NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (event_id),
    CONSTRAINT fk_events_to_categories FOREIGN KEY (category_id) REFERENCES categories(category_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_events_to_users FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

-- создаём таблицу participation_requests (с запросами на участие в событии):
CREATE TABLE IF NOT EXISTS participation_requests(
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    request_status varchar(20) NOT NULL,
    creation_date timestamp,
    CONSTRAINT pk_participation_request PRIMARY KEY (request_id),
    CONSTRAINT uq_request UNIQUE (user_id, event_id),
    CONSTRAINT fk_participation_requests_to_users FOREIGN KEY (user_id) REFERENCES users(user_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_participation_requests_to_events FOREIGN KEY (event_id) REFERENCES events(event_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

-- создаём таблицу compilations (с подборками событий):
CREATE TABLE IF NOT EXISTS compilations(
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    pinned boolean NOT NULL,
    title varchar(50),
    CONSTRAINT pk_compilation PRIMARY KEY (compilation_id),
    CONSTRAINT uq_title UNIQUE (title)
);

-- создаём таблицу event_compilations (связывает events и compilations):
CREATE TABLE IF NOT EXISTS event_compilations(
    compilation_id BIGINT,
    event_id BIGINT,
    CONSTRAINT fk_event_compilations_to_compilations FOREIGN KEY (compilation_id) REFERENCES compilations(compilation_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_event_compilations_to_events FOREIGN KEY (event_id) REFERENCES events(event_id)
    ON DELETE CASCADE ON UPDATE CASCADE
);