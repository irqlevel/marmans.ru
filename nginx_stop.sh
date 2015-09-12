#!/bin/bash -xv
NGINX_DIR=/root/nginx
NGINX_PID="$(cat $NGINX_DIR/logs/nginx.pid)"
kill $NGINX_PID
