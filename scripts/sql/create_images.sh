#!/bin/bash
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
export PGPASSWORD=1q2w3e
psql -h 127.0.0.1 -p 5432 -U test_user -d test_database -a -f $DIR/create_images.sql
