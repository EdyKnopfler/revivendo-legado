#!/bin/sh
set -e
"${PREFIX}/bin/isql" -user sysdba "${DBPATH}/${FIREBIRD_DATABASE}" -i /docker-entrypoint-initdb.d/sql/Metadados.sql
