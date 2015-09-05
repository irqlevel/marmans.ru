#!/bin/bash -xv
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
$DIR/activator clean stage
secret="$(openssl rand -base64 32)"
nohup $DIR/target/universal/stage/bin/myapp -Dplay.crypto.secret=$secret -Dhttp.port=80 -Dhttp.address=0.0.0.0 -Dpidfile.path=$DIR/prod_pid > prod_out.log 2>&1 &

