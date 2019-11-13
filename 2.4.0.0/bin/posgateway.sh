#!/bin/sh

JAVA_HOME="/usr/java/jdk1.6.0_22"
export JAVA_HOME

CLASSPATH="$CLASSPATH:$JAVA_HOME/lib/tools.jar:."
export CLASSPATH

$JAVA_HOME/bin/java -Djava.ext.dirs=lib:$JAVA_HOME/jre/lib/ext  -Djava.security.manager -Djava.security.policy=policy.all -jar pmposserver.jar  start
