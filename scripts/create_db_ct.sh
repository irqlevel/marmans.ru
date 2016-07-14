#!/bin/bash
rm -rf /var/lib/dbstore
mkdir -p /var/lib/dbstore
docker pull paintedfox/postgresql
docker stop db
docker rm db
docker run -d --name="db" -p 127.0.0.1:5432:5432 -v /var/lib/dbstore:/data -e USER="super" -e DB="dbname" -e PASS="1q2w3e" paintedfox/postgresql
sleep 5
sql/create_all.sh
