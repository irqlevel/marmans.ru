#!/bin/bash -xv
NGINX_DIR=/root/nginx
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
nohup $NGINX_DIR/sbin/nginx -c $DIR/nginx/nginx.conf > $DIR/logs/nginx.out.log 2>&1 &
