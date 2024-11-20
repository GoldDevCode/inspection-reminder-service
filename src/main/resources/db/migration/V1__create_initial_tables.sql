CREATE TABLE users
(
    id    UUID NOT NULL,
    email VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE vehicles
(
    id                   UUID NOT NULL,
    license_plate        VARCHAR(255),
    inspection_deadline  date,
    is_subscribed        BOOLEAN,
    is_notification_sent BOOLEAN,
    user_id              UUID,
    CONSTRAINT pk_vehicles PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE vehicles
    ADD CONSTRAINT uc_vehicles_licenseplate UNIQUE (license_plate);

ALTER TABLE vehicles
    ADD CONSTRAINT FK_VEHICLES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);