#!/bin/bash -xv
NGINX_DIR=/root/nginx
NGINX_PID="$(cat $NGINX_DIR/logs/nginx.pid)"
kill $NGINX_PID

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
PID="$(cat $DIR/prod_pid)"
kill $PID
