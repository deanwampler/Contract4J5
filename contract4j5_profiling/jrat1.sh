#!/bin/bash
#--------------------------------------------------------
# jrat1.sh - First step for running JRat on a sample application.
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
mkdir -p jrat/lib
cp -r bin jrat
cp -r ../contract4j5/contract4j5.jar jrat
cp -r ../contract4j5/lib/*.jar       jrat/lib

CLASSPATH=jrat/contract4j5.jar:$ASPECTJ_HOME/lib/aspectjrt.jar
for jar in jrat/lib/*.jar
do
	CLASSPATH=$CLASSPATH:$jar
done
ajc -Xms500m -Xmx1000m -1.5 -source 1.5 -aspectpath jrat/contract4j5.jar -inpath jrat/bin -cp $CLASSPATH -outjar jrat/out.jar
