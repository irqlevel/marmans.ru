#!/bin/bash -xv
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
./activator clean stage
secret="$(openssl rand -base64 32)"
target/universal/stage/bin/myapp -Dplay.crypto.secret=$secret -Dhttp.port=80 -Dhttp.address=0.0.0.0 -Dpidfile.path=$DIR/prod_pid
