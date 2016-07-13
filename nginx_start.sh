#!/bin/bash -xv
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
NGINX_DIR=$DIR/nginx
nohup $NGINX_DIR/sbin/nginx -c $NGINX_DIR/conf/nginx.conf > $NGINX_DIR/logs/nginx.out.log 2>&1 &
