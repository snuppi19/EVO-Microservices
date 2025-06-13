-- Tạo database cho IAM Service
CREATE
DATABASE "KeyCloak_Fake";

-- Tạo database cho Storage Service
CREATE
DATABASE "StorageService";

CREATE TABLE IF NOT EXISTS invalidate_token
(
    id
    TEXT
    PRIMARY
    KEY,
    expiry_time
    TIMESTAMP
);

