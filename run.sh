#!/bin/bash

set -e

MAVEN_OPTS="$MAVEN_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9000"
MAVEN_OPTS="$MAVEN_OPTS -agentpath:$HOME/programs/jrebel/lib/libjrebel64.so"

export MAVEN_OPTS
echo
echo "MAVEN_OPTS=$MAVEN_OPTS"
exec mvn tomcat7:run $@
