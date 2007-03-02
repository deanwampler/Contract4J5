#!/bin/bash
#--------------------------------------------------------
# jhat.sh - Run the JDK v6 jhat on the output of hprof
#
# Assumes you have built with eclipse and the .class files
# are in the "bin" directory. If not, change the CLASSPATH
# definition appropriately.

. ../contract4j5/mysetup.sh
. ../contract4j5/env.sh

# Run under java 6 to get "jhat".
export JAVA_HOME=/opt/jdk1.6
export PATH=$JAVA_HOME/bin:$PATH

date

CLASSPATH=bin
for f in ../contract4j5/contract4j5.jar ../contract4j5/lib/*.jar 
do
	CLASSPATH=$CLASSPATH:$f
done
export CLASSPATH
#echo $CLASSPATH

jhat -J-Xmx1000m java.hprof

