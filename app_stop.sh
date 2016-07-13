#!/bin/bash -xv
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
PID="$(cat $DIR/logs/app.pid)"
kill $PID
