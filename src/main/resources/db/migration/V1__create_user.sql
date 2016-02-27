CREATE TABLE "users" (
  "id"       VARCHAR PRIMARY KEY,
  "username" VARCHAR NOT NULL UNIQUE,
  "role"     VARCHAR NOT NULL,
  "password" VARCHAR NOT NULL,
  "email"    VARCHAR UNIQUE,
  "created"  TIMESTAMPTZ NOT NULL,
  "modified" TIMESTAMPTZ NOT NULL
);
