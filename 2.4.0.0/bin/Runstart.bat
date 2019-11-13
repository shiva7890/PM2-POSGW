set JAVA="E:\jdk1.8.0_192"
set CLASSPATH="C:\Program Files\Java\jdk1.8.0_192\lib\tools.jar;.;"
%JAVA% -Djava.ext.dirs=lib  -Djava.security.manager -Djava.security.policy=policy.all -jar pmposserver.jar start
pause
