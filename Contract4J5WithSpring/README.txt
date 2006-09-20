Contract4J5WithSpring 0.6.0 README  

v0.6.0.0   October 1, 2006

Dean Wampler 
http://www.contract4j.org
http://www.aspectprogramming.com/contract4j

This directory contains a separate example demonstrating how to use the Spring
Framework's Dependency Injection (DI) to configure the properties of Contract4J.
It is handled separately so Spring is not required for those people not using 
it.

To build the Spring example, go to the "../contract4j5" directory. Make sure the
"contract4j5.jar" exists or build it if it doesn't exist, using one of the "all"
commands documented in the README.txt in that directory. Once the jar is built,
then run one of the following commands:

	2a) ./build.sh all.spring (*nix)
	2b) build.bat all.spring  (windows)
or
	2c) ant all.spring

This will run a test target that confirms that Spring can "wire" Contract4J
correctly. See the following files for more details:

test/org/contract4j5/configurator/spring/test/ConstructWithSpringTest.java -
	Uses Spring's "ApplicationContext" to construct C4J, then tests that the
	components and properties are wired as expected.
test/conf/applicationContext-contract4j5.xml - The configuration file
test/conf/contract4j.properties - A properties files used by the config. file.
