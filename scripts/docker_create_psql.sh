#!/bin/bash
rm -rf /var/lib/dbstore
mkdir -p /var/lib/dbstore
docker pull paintedfox/postgresql
docker stop postgresql
docker rm postgresql
docker run -d --name="postgresql" -p 127.0.0.1:5432:5432 -v /var/lib/dbstore:/data -e USER="super" -e DB="dbname" -e PASS="1q2w3e" paintedfox/postgresql
docker logs postgresql
