#!/bin/bash
export PGPASSWORD=1q2w3e
psql -h 127.0.0.1 -p 5432 -U test_user -d test_database -a -f create_all.sql
