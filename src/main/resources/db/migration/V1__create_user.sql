CREATE TABLE "users" (
  "id"       VARCHAR PRIMARY KEY,
  "username" VARCHAR NOT NULL,
  "role"     VARCHAR NOT NULL,
  "password" VARCHAR NOT NULL,
  "email"    VARCHAR,
  "created"  TIMESTAMPTZ NOT NULL,
  "modified" TIMESTAMPTZ NOT NULL
);
