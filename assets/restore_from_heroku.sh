#!/usr/bin/env bash
/Applications/Postgres.app/Contents/Versions/10/bin/pg_restore --verbose --clean --no-acl --no-owner -h localhost -p 5442 -U postgres -d pankal
