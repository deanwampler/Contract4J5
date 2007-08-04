#!/bin/bash
#--------------------------------------------------------
# jrat.sh - Run JRat on a sample application.
#
# Assumes you have built with eclipse and the .class files
# are in the "bin" directory. If not, change the CP
# definition appropriately.

. ../contract4j5/mysetup.sh
. ../contract4j5/env.sh

export JRAT_HOME="$JAVA_TOOLS_HOME/jrat"
export PATH="$PATH:$JRAT_HOME/bin"
date

rm -rf jrat
mkdir jrat
cp -r bin jrat
cp -r ../contract4j5/contract4j5.jar ../contract4j5/lib/*.jar jrat

$CP=jrat/bin
for jar in jrat/*.jar
do
	CP=$CP:$jar
done
export CLASSPATH=$CLASSPATH:$CP::$JAVA_TOOLS_HOME/jrat/shiftone-jrat.jar
export ASPECTPATH="ASPECTPATH:jrat/contract4j5.jar"

for interp in "jexl" "groovy" "jruby" "stub" "nobsf"
do
	echo "running with the expression interpreter \"$interp\". Output written to jrat.$interp.log:"
	rm -f jrat/out.jar
	ajc -aspectpath jrat/contract4j5.jar
	java -ea -javaagent:$ASPECTJ_HOME/lib/aspectjweaver.jar -Djrat.factory=org.shiftone.jrat.provider.tree.TreeMethodHandlerFactory -cp $CLASSPATH org.contract4j5.performance.test.Person 10000 $interp > jrat.$interp.log
	echo "jrat.$interp.log:"
	cat jrat.$interp.log
done

#java -jar $JAVA_TOOLS_HOME/jrat/shiftone-jrat.jar &
