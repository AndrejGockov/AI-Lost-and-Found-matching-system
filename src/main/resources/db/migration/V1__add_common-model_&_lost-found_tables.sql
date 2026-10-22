-- Enum replacing the Item_Status table
CREATE TYPE item_status AS ENUM ('UNRESOLVED', 'RESOLVED', 'PERMANENTLY_LOST');

CREATE TABLE auth_user
(
    id      VARCHAR(255) NOT NULL PRIMARY KEY,
    alias text,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(255) NOT NULL UNIQUE,
    role text not null ,
    active boolean default true not null
);

CREATE TABLE lost_item
(
    id            varchar(255) PRIMARY KEY,
    poster_id     varchar(255) NOT NULL REFERENCES auth_user (id) ON DELETE restrict ,
    title         VARCHAR(255) NOT NULL,
    description   TEXT         NOT NULL,
    date_posted   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_updated  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    is_updated    BOOLEAN      NOT NULL DEFAULT FALSE,
    date_lost     TIMESTAMPTZ,
    location_lost TEXT,
    status        item_status  NOT NULL DEFAULT 'UNRESOLVED',
    embedding     float[]
);

CREATE TABLE lost_item_image
(
    id         varchar(225) primary key,
    item_id    varchar(225) references lost_item (id) on delete cascade,
    img_bytes  bytea not null,
    size_bytes INT   NOT NULL CHECK (size_bytes > 0 AND size_bytes <= 20485760) -- 20 MB

);

CREATE TABLE found_item
(
    id              varchar(255) PRIMARY KEY,
    poster_id       varchar(255)       NOT NULL REFERENCES auth_user (id) ON DELETE restrict ,
    holder_id       varchar(255)       NOT NULL REFERENCES auth_user (id) ON DELETE RESTRICT,
    title           VARCHAR(255) NOT NULL,
    description     TEXT         NOT NULL,
    date_posted     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_updated    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    is_updated      BOOLEAN      NOT NULL DEFAULT FALSE,
    date_found      TIMESTAMPTZ,
    location_found  TEXT,
    is_identifiable BOOLEAN      NOT NULL DEFAULT FALSE,
    status          item_status  NOT NULL DEFAULT 'UNRESOLVED',
    embedding       float[]
);

CREATE TABLE found_item_image
(
    id         varchar(225) primary key,
    item_id    varchar(225) references lost_item (id) on delete cascade,
    size_bytes INT   NOT NULL CHECK (size_bytes > 0 AND size_bytes <= 20485760), -- 20 MB
    img_bytes  bytea not null
);

CREATE TABLE lost_found_message
(
    id          varchar(255) PRIMARY KEY,
    sender_id   varchar(255) NOT NULL REFERENCES auth_user (id) ON DELETE restrict,
    receiver_id varchar(255) NOT NULL REFERENCES auth_user (id) ON DELETE restrict,
    content     TEXT         NOT NULL,
    date_sent   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_message_not_self CHECK (sender_id <> receiver_id)
);

CREATE TABLE lost_found_message_attachment
(
    id               varchar(255) PRIMARY KEY,
    message_id       varchar(255) NOT NULL REFERENCES lost_found_message (id) ON DELETE CASCADE,
    size_bytes       INT          NOT NULL CHECK (size_bytes > 0 AND size_bytes <= 20485760), -- 20 MB
    attachment_bytes bytea        NOT NULL
);