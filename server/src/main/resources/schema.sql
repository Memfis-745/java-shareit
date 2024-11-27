DROP TABLE IF EXISTS users, requests, items, bookings, comments;

CREATE TABLE IF NOT EXISTS users (
  id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name  VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(512) NOT NULL,
    user_id      BIGINT       NOT NULL
        REFERENCES users (id) ON DELETE CASCADE,
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_requests PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(512) NOT NULL,
    available   BOOLEAN      NOT NULL,
    user_id     BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    request_id  BIGINT REFERENCES requests (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id     BIGINT NOT NULL
            REFERENCES items (id) ON DELETE CASCADE,
    booker_id   BIGINT NOT NULL
        CONSTRAINT bookings_users_id_fk
            REFERENCES users (id) ON DELETE CASCADE,
    status      VARCHAR(50),
    CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text            VARCHAR(600)  NOT NULL,
    item_id         BIGINT        NOT NULL
        CONSTRAINT comments_items_id_fk
              REFERENCES items (id) ON DELETE CASCADE,
    author_id       BIGINT  NOT NULL
        CONSTRAINT comments_users_id_fk
            REFERENCES users (id) ON DELETE CASCADE,
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comm PRIMARY KEY (id)
);

