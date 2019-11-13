set JAVA_HOME=C:\jdk1.5.0_14
set JAVA=%JAVA_HOME%\bin\java
set CLASSPATH=%JAVA_HOME%\lib\tools.jar;.;
%JAVA% -Djava.ext.dirs=lib  -Djava.security.manager -Djava.security.policy=policy.all -jar pmposserver.jar stop
