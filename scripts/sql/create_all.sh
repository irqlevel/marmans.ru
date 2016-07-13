#!/bin/bash
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
export PGPASSWORD=1q2w3e
psql -h 127.0.0.1 -p 5432 -U super -d dbname -a -f $DIR/create_all.sql
