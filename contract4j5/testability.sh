#!/bin/bash
# Run Testability Explorer V1.2 on Contract4J. Build C4J first.
# Download it from http://code.google.com/p/testability-explorer/
# Define $TESTABILITY_EXPLORER_HOME to point to the installation location.
# Options:
#  -print (html|detail)
# See also options documented in the TE readme.

classpath=src-classes:test-classes:
for jar in lib/*.jar $ASPECTJ_HOME/lib/*.jar
do
	test -s $jar &&	classpath=$classpath:$jar
done
echo "Invoking: java -jar $TESTABILITY_EXPLORER_HOME/testability-explorer-1.2.0-r54.jar -cp $classpath org.contract4j5 $@" 1>&2
java -jar $TESTABILITY_EXPLORER_HOME/testability-explorer-1.2.0-r54.jar -cp $classpath org.contract4j5 "$@"
