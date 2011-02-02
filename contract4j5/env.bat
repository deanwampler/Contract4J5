rem Needs updating!

set DRIVE=c:
set ANT_HOME=%DRIVE%\Apache\apache-ant-1.6.5
set JAVA_HOME=%DRIVE%\Program Files\Java\jdk1.5.0_04
set JUNIT_HOME=%DRIVE%\tools\javatools\junit3.8.1
set ASPECTJ_HOME=%DRIVE%\aspectj1.5.3
rem Ignore the SPRING_HOME if you aren't building the separate Spring example
set SPRING_HOME=%DRIVE%\Spring\spring-framework-1.2.5

rem set CONTRACT4J5_ROOT=%HOME%\src\java\contract4j5
set CONTRACT4J5_ROOT=..\
set CONTRACT4J5_HOME=%CONTRACT4J_ROOT%\contract4j5
set CONTRACT4J5_LIB=%CONTRACT4J_HOME%\lib
set CONTRACT4J5_SPRING_HOME=%CONTRACT4J5_ROOT%\contract4j5WithSpring

rem The JRuby 1.0.1 libraries to the classpath (edit as needed...)
CLASSPATH=%CONTRACT4J5_HOME%\lib\jruby-complete-1.0.1.jar;%CLASSPATH%
CLASSPATH=%CONTRACT4J5_HOME%\lib\asm-2.2.jar;%CLASSPATH%
CLASSPATH=%CONTRACT4J5_HOME%\lib\antlr-2.7.5.jar;%CLASSPATH%
CLASSPATH=%CONTRACT4J5_HOME%\lib\bsf-2.4.0.jar;%CLASSPATH%
CLASSPATH=%CONTRACT4J5_HOME%\lib\commons-jexl-1.1.jar;%CLASSPATH%
CLASSPATH=%CONTRACT4J5_HOME%\lib\commons-logging.jar;%CLASSPATH%

rem The other libs, added after JRuby's.
CLASSPATH=%ANT_HOME%\lib\ant.jar;%ASPECTJ_HOME%\lib\aspectjrt.jar;%ASPECTJ_HOME%\lib\aspectjtools.jar;%JUNIT_HOME%\junit.jar;%SPRING_HOME%\dist\spring.jar;%CLASSPATH%

set PATH=%JAVA_HOME%\bin;%ASPECTJ_HOME%\bin;%ANT_HOME%\bin;%PATH%
