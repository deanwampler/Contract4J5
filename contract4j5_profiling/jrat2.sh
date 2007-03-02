#!/bin/bash
#--------------------------------------------------------
# jrat2.sh - Run the second step for JRat on a sample application.
#
# Assumes you run jrat1.sh and instrumented the output of that
# pass with the jrat tool.

. ../contract4j5/mysetup.sh
. ../contract4j5/env.sh

export JRAT_HOME="$JAVA_TOOLS_HOME/jrat"
export PATH="$PATH:$JRAT_HOME/bin"
date

$CP=jrat/out.jar:jrat/bin
for jar in jrat/*.jar
do
	CP=$CP:$jar
done
export CLASSPATH=$CLASSPATH:$CP::$JAVA_TOOLS_HOME/jrat/shiftone-jrat.jar

for interp in "groovy" "nobsf" "nobsfjexl"
do
	echo "running with the expression interpreter \"$interp\". Output written to jrat.$interp.log:"
	java -Djrat.factory=org.shiftone.jrat.provider.tree.TreeMethodHandlerFactory -cp $CLASSPATH org.contract4j5.performance.test.Person 100 $interp > jrat.$interp.log
	echo "jrat.$interp.log:"
	cat jrat.$interp.log
done

#java -jar $JAVA_TOOLS_HOME/jrat/shiftone-jrat.jar &
