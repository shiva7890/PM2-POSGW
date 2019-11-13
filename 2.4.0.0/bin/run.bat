set JAVA_HOME=E:\jdk1.8.0_192
set JAVA=%JAVA_HOME%\bin\java
set CLASSPATH=%JAVA_HOME%\lib\tools.jar;.;
%JAVA% -Djava.ext.dirs=lib;$JAVA_HOME/jre/lib/ext  -Djava.security.manager -Djava.security.policy=policy.all -jar E:\TelefonicaDevProjects\2.4.0.0\pmposserver.jar
pause
