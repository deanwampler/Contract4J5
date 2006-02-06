set ANT_HOME=c:\Apache\apache-ant-1.6.2
set JAVA_HOME=c:\Program Files\Java\jdk1.5.0_04
set JUNIT_HOME=c:\tools\javatools\junit3.8.1
set ASPECTJ_HOME=c:\aspectj1.5
set JEXL_HOME=c:\Apache\jakarta\commons\commons-jexl-1.0
set CONTRACT4J_HOME=%HOME%\src\java\contract4j5_0_5_0\contract4j5

set CLASSPATH=%ANT_HOME%\lib\ant.jar;%ASPECTJ_HOME%\lib\aspectjrt.jar;%ASPECTJ_HOME%\lib\aspectjtools.jar;%JUNIT_HOME%\junit.jar;%JEXL_HOME\commons-jexl-1.0.jar;%CLASSPATH%
set PATH=%JAVA_HOME%\bin;%ASPECTJ_HOME%\bin;%ANT_HOME%\bin;%PATH%

ant %1 %2 %3 %4 %5 %6 %7 %8 %9
