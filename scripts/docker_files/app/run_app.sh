#!/bin/bash -xv
ACTIVATOR=/distr/activator/bin/activator
APPDIR=/appstore
cd $APPDIR
$ACTIVATOR clean stage
secret="$(openssl rand -base64 32)"
mkdir -p $APPDIR/logs
$APPDIR/target/universal/stage/bin/myapp -Dplay.crypto.secret=$secret -Dhttp.port=8080 -Dhttp.address=0.0.0.0 -Dpidfile.path=$APPDIR/logs/app.pid > $APPDIR/logs/app_out.log 2>&1
