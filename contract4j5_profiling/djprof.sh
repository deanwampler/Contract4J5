#!/bin/bash
#--------------------------------------------------------
# ejprof.sh - Run DJProf on a sample application.
#
# Assumes you have built with eclipse and the .class files
# are in the "bin" directory. If not, change the CLASSPATH
# definition appropriately.

. ../contract4j5/mysetup.sh
. ../contract4j5/env.sh

export DJPROF_HOME="$JAVA_TOOLS_HOME/djprof-v1.0"
export PATH="$PATH:$DJPROF_HOME/bin"
date

CLASSPATH=bin
for f in $CONTRACT4J5_ROOT/contract4j5/bin $CONTRACT4J5_ROOT/contract4j5/lib/*.jar 
do
	CLASSPATH=$CLASSPATH:$f
done
export CLASSPATH
export ASPECTPATH="ASPECTPATH:$CONTRACT4J5_ROOT/contract4j5/contract4j5.jar"

which_opt=lifetime
if [ $# -gt 0 ] ; then
	which_opt=$1
	shift
fi

for interp in "jexl" "groovy" "jruby" "stub" "nobsfjexl" "nobsf"
do
	echo "running with the expression interpreter \"$interp\". Output written to djprof.$which_opt.$interp.log:"
	djprof -$which_opt -period 1 -o djprof.$which_opt.$interp.log -cp $CLASSPATH  org.contract4j5.performance.test.Person 10 $interp
	echo "djprof.$which_opt.$interp.log:"
	cat djprof.$which_opt.$interp.log
done