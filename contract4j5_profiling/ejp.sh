#!/bin/bash
#--------------------------------------------------------
# ejp.sh - Run EJP on a sample application.
#
# Assumes you have built with eclipse and the .class files
# are in the "bin" directory. If not, change the CLASSPATH
# definition appropriately.

. ../contract4j5/mysetup.sh
. ../contract4j5/env.sh

EJP_HOME="$JAVA_TOOLS_HOME/ejp"
case "$OS_NAME" in
		cygwin*)
				export PATH="$PATH;$EJP_HOME/lib/tracer.dll"
				;;
		linux*)
				export LD_LIBRARY_PATH="$LD_LIBRARY_PATH;$EJP_HOME/lib"
				;;
		darwin*)
				export DYLD_LIBRARY_PATH="$DYLD_LIBRARY_PATH;$EJP_HOME/lib"
				;;
esac

date

CLASSPATH=bin
for f in $CONTRACT4J5_ROOT/contract4j5/contract4j5.jar $CONTRACT4J5_ROOT/contract4j5/lib/*.jar 
do
	CLASSPATH=$CLASSPATH:$f
done
export CLASSPATH
#echo $CLASSPATH

# Very lame: it appears you have to run it from the following directory, due
# to hard-coded paths in EJP!
cd $EJP_HOME/bin


java -ea -javaagent:$ASPECTJ_HOME/lib/aspectjweaver.jar -Xruntracer org.contract4j5.performance.test.Person "$@"

#java -Xmx1000m -jar ../lib/presenter.jar
../bin/Presenter.sh