CREATE TABLE "blog_post" (
  "id"         VARCHAR PRIMARY KEY,
  "author_id"  VARCHAR NOT NULL,
  "published"  BOOLEAN NOT NULL,
  "title"      VARCHAR NOT NULL,
  "slug"       VARCHAR NOT NULL UNIQUE,
  "intro"      VARCHAR NOT NULL,
  "content"    TEXT NOT NULL,
  "created"    TIMESTAMPTZ NOT NULL,
  "modified"   TIMESTAMPTZ NOT NULL
);
