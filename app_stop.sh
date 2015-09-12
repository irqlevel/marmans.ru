#!/bin/bash -xv
/bin/bash nginx_stop.sh
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
PID="$(cat $DIR/logs/app.pid)"
kill $PID
