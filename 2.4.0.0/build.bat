set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_192

set MY_HOME=E:\TelefonicaDevProjects

set ANT_ARGS=-Dbasedir="%MY_HOME%"

set ANT_HOME=E:\apache-ant-1.9.14

set MYSERVERCLASSES=%MY_HOME%\lib

set thrdjars=%thrdjars%;%ANT_HOME%\lib\ant.jar;%ANT_HOME%\lib\optional.jar;%ANT_HOME%\lib\xercesImpl.jar;%ANT_HOME%\lib\xml-apis.jar;

set MYCLASSPATH=%thrdjars%;%JAVA_HOME%\jre\lib\rt.jar;%MYSERVERCLASSES%\rtsxmlbeans.jar;%MYSERVERCLASSES%\eventxmlbeans.jar;%MYSERVERCLASSES%\commons-net-1.3.0.jar;%MYSERVERCLASSES%\log4j-1.2.6.jar;%MYSERVERCLASSES%\javax.servlet.jar;%MYSERVERCLASSES%\classes12.zip;%MYSERVERCLASSES%\com.ibm.mq.jar;%MYSERVERCLASSES%\mail.jar;%MYSERVERCLASSES%\activation.jar;%MY_HOME%\classes\airejb;%MY_HOME%\classes\aircls;%MY_HOME%\classes\airdao;%MYSERVERCLASSES%\classes12.zip;%MYSERVERCLASSES%\jboss-j2ee.jar;%MYSERVERCLASSES%\junit-3.8.1.jar;%MYSERVERCLASSES%\posejbproxys.jar;%MYSERVERCLASSES%\posdtos.jar;%MYSERVERCLASSES%\external.jar;%MYSERVERCLASSES%;.;

set PATH=%ANT_HOME%\bin

echo %CLASSPATH%

ant -buildfile build.xml

pause