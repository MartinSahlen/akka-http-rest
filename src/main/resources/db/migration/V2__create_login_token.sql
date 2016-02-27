CREATE TABLE "login_token" (
  "token"        VARCHAR PRIMARY KEY,
  "user_id"   VARCHAR NOT NULL,
  "last_used" TIMESTAMP NOT NULL,
  "created"  TIMESTAMP NOT NULL,
  "modified" TIMESTAMP NOT NULL
);
