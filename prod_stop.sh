#!/bin/bash -xv
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
if [ ! -f $DIR/prod_pid ]; then
	echo "no pid exists"
	exit
fi
PID="$(cat $DIR/prod_pid)"
kill $PID
