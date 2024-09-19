#!/bin/bash

# Load environment variables from the .env file in a subshell
(
    export $(grep -v '^#' .env | xargs)

    docker exec -it postgres_db psql -U "$DB_USERNAME" -d "$DB_TABLE_NAME" -c "\dt" -c "SELECT * FROM search_query_object;"
)