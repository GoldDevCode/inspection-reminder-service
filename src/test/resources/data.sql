-- Generate sample data by INSERTS statements for users and vehicles tables

INSERT INTO users (id, email)
VALUES ('66572299-ba71-42d6-bdd3-470f6f3c9233', 'abc@gmail.com');
INSERT INTO users (id, email)
VALUES ('dff1c0b4-e689-4a56-8abf-db01ad36955d', 'xyz@gmail.com');
INSERT INTO users (id, email)
VALUES ('31b89900-f206-4e74-857a-cd8bd4af60d9', 'def@gmail.com');

INSERT INTO vehicles (id, license_plate, inspection_deadline, is_subscribed, is_notification_sent, user_id)
VALUES ('8d7d3fb3-c864-4c0a-b5b2-65d66033386d', 'EB11111', '2025-12-01', true, false,
        '66572299-ba71-42d6-bdd3-470f6f3c9233');
INSERT INTO vehicles (id, license_plate, inspection_deadline, is_subscribed, is_notification_sent, user_id)
VALUES ('3fe2548c-576a-48a9-b94d-da054fa0d363', 'EB22222', '2026-06-12', true, false,
        '66572299-ba71-42d6-bdd3-470f6f3c9233');
INSERT INTO vehicles (id, license_plate, inspection_deadline, is_subscribed, is_notification_sent, user_id)
VALUES ('634da307-ffab-46c7-ad31-c0811c3f4c4a', 'EB33333', '2025-07-31', true, false,
        'dff1c0b4-e689-4a56-8abf-db01ad36955d');
INSERT INTO vehicles (id, license_plate, inspection_deadline, is_subscribed, is_notification_sent, user_id)
VALUES ('62e02c5e-5441-49fc-9e51-665068ba66ce', 'EB44444', '2027-05-31', true, false,
        'dff1c0b4-e689-4a56-8abf-db01ad36955d');
INSERT INTO vehicles (id, license_plate, inspection_deadline, is_subscribed, is_notification_sent, user_id)
VALUES ('9ee9be9c-7916-49e7-b246-6619c87297bf', 'EB55555', '2025-12-01', false, false,
        '31b89900-f206-4e74-857a-cd8bd4af60d9');
INSERT INTO vehicles (id, license_plate, inspection_deadline, is_subscribed, is_notification_sent, user_id)
VALUES ('04560762-f1a9-4b7a-bd0a-eb1060f8223a', 'EB66666', '2026-12-01', true, false,
        '31b89900-f206-4e74-857a-cd8bd4af60d9');