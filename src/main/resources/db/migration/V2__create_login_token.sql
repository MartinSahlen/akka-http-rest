CREATE TABLE "login_tokens" (
  "token"        VARCHAR PRIMARY KEY,
  "user_id"   VARCHAR NOT NULL,
  "last_used" TIMESTAMP NOT NULL,
  "created"  TIMESTAMPTZ NOT NULL,
  "modified" TIMESTAMPTZ NOT NULL
);
