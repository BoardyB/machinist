DROP SCHEMA IF EXISTS machinist CASCADE;

CREATE SCHEMA machinist;

CREATE TABLE machinist.machine
(
  id                   VARCHAR(50) PRIMARY KEY NOT NULL,
  created_at           TIMESTAMP,
  updated_at           TIMESTAMP,
  name                 VARCHAR(100),
  description          VARCHAR(1000),
  year_of_production   SMALLINT,
  deleted              BOOLEAN
);