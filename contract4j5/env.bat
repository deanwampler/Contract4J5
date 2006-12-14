set DRIVE=c:
set ANT_HOME=%DRIVE%\Apache\apache-ant-1.6.2
set JAVA_HOME=%DRIVE%\Program Files\Java\jdk1.5.0_04
set JUNIT_HOME=%DRIVE%\tools\javatools\junit3.8.1
set ASPECTJ_HOME=%DRIVE%\aspectj1.5.2a
rem Ignore the SPRING_HOME if you aren't building the separate Spring example
set SPRING_HOME=%DRIVE%\Spring\spring-framework-1.2.5

set CONTRACT4J5_ROOT=%HOME%\src\java\contract4j5
set CONTRACT4J5_HOME=%CONTRACT4J_ROOT%\contract4j5
set CONTRACT4J5_LIB=%CONTRACT4J_HOME%\lib
set CONTRACT4J5_SPRING_HOME=%HOME%\src\java\contract4j5\contract4j5WithSpring

set CLASSPATH=%ANT_HOME%\lib\ant.jar;%ASPECTJ_HOME%\lib\aspectjrt.jar;%ASPECTJ_HOME%\lib\aspectjtools.jar;%JUNIT_HOME%\junit.jar;%SPRING_HOME%\dist\spring.jar;%CLASSPATH%

set PATH=%JAVA_HOME%\bin;%ASPECTJ_HOME%\bin;%ANT_HOME%\bin;%PATH%
