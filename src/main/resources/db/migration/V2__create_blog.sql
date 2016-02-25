CREATE TABLE "blog" (
  "id"         VARCHAR PRIMARY KEY,
  "author_id"  VARCHAR NOT NULL,
  "published"  BOOLEAN NOT NULL,
  "title"      VARCHAR NOT NULL,
  "slug"       VARCHAR NOT NULL,
  "intro"      VARCHAR NOT NULL,
  "image"      VARCHAR,
  "content"    TEXT NOT NULL
);
