rem Needs updating!

set DRIVE=c:
set ANT_HOME=%DRIVE%\Apache\apache-ant-1.6.5
set JAVA_HOME=%DRIVE%\Program Files\Java\jdk1.5.0_04
set JUNIT_HOME=%DRIVE%\tools\javatools\junit3.8.1
set ASPECTJ_HOME=%DRIVE%\aspectj1.5.3
set JRUBY_HOME=%DRIVE%\jruby-1.0.1
rem Ignore the SPRING_HOME if you aren't building the separate Spring example
set SPRING_HOME=%DRIVE%\Spring\spring-framework-1.2.5

set CONTRACT4J5_ROOT=%HOME%\src\java\contract4j5
set CONTRACT4J5_HOME=%CONTRACT4J_ROOT%\contract4j5
set CONTRACT4J5_LIB=%CONTRACT4J_HOME%\lib
set CONTRACT4J5_SPRING_HOME=%HOME%\src\java\contract4j5\contract4j5WithSpring

rem The JRuby 1.0.1 libraries to the classpath (edit as needed...)
CLASSPATH=%ANT_HOME%\lib\jruby.jar;%CLASSPATH%
CLASSPATH=%ANT_HOME%\lib\jline-0.9.91.jar;%CLASSPATH%
CLASSPATH=%ANT_HOME%\lib\jarjar-0.7.jar;%CLASSPATH%
CLASSPATH=%ANT_HOME%\lib\bsf.jar;%CLASSPATH%
CLASSPATH=%ANT_HOME%\lib\backport-util-concurrent.jar;%CLASSPATH%
CLASSPATH=%ANT_HOME%\lib\asm-commons-2.2.3.jar;%CLASSPATH%
CLASSPATH=%ANT_HOME%\lib\asm-2.2.3.jar;%CLASSPATH%

rem The other libs, added after JRuby's.
CLASSPATH=%ANT_HOME%\lib\ant.jar;%ASPECTJ_HOME%\lib\aspectjrt.jar;%ASPECTJ_HOME%\lib\aspectjtools.jar;%JUNIT_HOME%\junit.jar;%SPRING_HOME%\dist\spring.jar;%CLASSPATH%

set PATH=%JAVA_HOME%\bin;%ASPECTJ_HOME%\bin;%ANT_HOME%\bin;%PATH%
