#!/bin/bash -xv
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
$DIR/activator clean stage
secret="$(openssl rand -base64 32)"
nohup $DIR/target/universal/stage/bin/myapp -Dplay.crypto.secret=$secret -Dhttp.port=8080 -Dhttp.address=127.0.0.1 -Dpidfile.path=$DIR/logs/app.pid > $DIR/logs/app_out.log 2>&1 &
/bin/bash nginx_start.sh
