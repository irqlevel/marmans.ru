#!/bin/bash
rm -rf /var/lib/appstore
mkdir -p /var/lib/appstore
cp -ar ../ /var/lib/appstore
pushd .
cd docker_files/app && docker build -t marmans.ru/app .
popd
docker stop app
docker rm app
docker run -d --name="app" -p 0.0.0.0:8080:8080 -v /var/lib/appstore:/appstore --link db:db marmans.ru/app
