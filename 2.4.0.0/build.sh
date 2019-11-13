export JAVA_HOME=/usr/java/jdk1.6.0_22
export ANT_HOME=/PM/pmdomestic/pmdomestic/ant

export MY_HOME=/PM/pmdomestic/pmdomestic/columbiaReleases/gateways/POSGW_Source/2.0.0.0

export MYCLASSPATH=$MY_HOME/lib:$thrdjars:$MYSERVERCLASSES/log4j-1.2.6.jar:$MYSERVERCLASSES/jbossall-client.jar:$MYSERVERCLASSES/jboss-j2ee.jar:$MYSERVERCLASSES/posejbproxys.jar:$MYSERVERCLASSES/posdtos.jar:$MYSERVERCLASSES/domesticservices.jar:$MYSERVERCLASSES/jpos.jar:.:

export PATH=$PATH:$ANT_HOME/bin:$JAVA_HOME/bin

export CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/jre/lib/rt.jar:.:$CLASSPATH

ant -buildfile build.xml
