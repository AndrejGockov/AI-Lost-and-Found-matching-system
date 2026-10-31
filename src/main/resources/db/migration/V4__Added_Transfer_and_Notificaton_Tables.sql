-- V3__add_offers_and_notifications.sql

CREATE TABLE transfer_found_item
(
    id                varchar(255) PRIMARY KEY,
    found_item_id     varchar(255) NOT NULL REFERENCES found_item (id) ON DELETE Restrict ,
    current_holder_id varchar(255) NOT NULL REFERENCES found_item (id) ON DELETE RESTRICT,
    new_holder_id     varchar(255) NOT NULL REFERENCES auth_user (id) ON DELETE RESTRICT,
    date_sent         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    date_responded    TIMESTAMPTZ,
    is_accepted       BOOLEAN, -- NULL = pending, TRUE = accepted, FALSE = declined
    CONSTRAINT chk_offer_not_self CHECK (current_holder_id <> new_holder_id),
    CONSTRAINT chk_offer_response_consistent
        CHECK ((is_accepted IS NULL) = (date_responded IS NULL)),
    CONSTRAINT chk_offer_response_after_sent
        CHECK (date_responded IS NULL OR date_responded >= date_sent)
);

CREATE TABLE lost_found_notification
(
    id           varchar(255) PRIMARY KEY,
    user_id      varchar(255) NOT NULL REFERENCES auth_user (id) ON DELETE CASCADE,
    message      TEXT         NOT NULL,
    date_created TIMESTAMPTZ  NOT NULL DEFAULT now(),
    is_viewed    BOOLEAN      NOT NULL DEFAULT FALSE
);