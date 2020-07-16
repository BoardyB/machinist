DROP SCHEMA IF EXISTS machinist;

CREATE SCHEMA machinist;

CREATE TABLE machinist.machine
(
  id            VARCHAR(50) PRIMARY KEY NOT NULL,
  created_at    TIMESTAMP,
  updated_at    TIMESTAMP,
  name          VARCHAR(1000),
  deleted       BOOLEAN
);