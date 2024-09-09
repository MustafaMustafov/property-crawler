CREATE TABLE IF NOT EXISTS "search_query_object" (
    id SERIAL PRIMARY KEY,
    search_object TEXT NOT NULL,
    generated_file BYTEA(MAX),
    timestamp TIMESTAMP NOT NULL
);

CREATE SEQUENCE search_query_object_id_sequence
    INCREMENT BY 1
    START WITH 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

ALTER TABLE "search_query_object"
    DROP COLUMN generated_file;
