#!/bin/bash
#--------------------------------------------------------
# hprof.sh - Run the JDK's hprof on a sample application.
#
# Assumes you have built with eclipse and the .class files
# are in the "bin" directory. If not, change the CLASSPATH
# definition appropriately.

. ../contract4j5/mysetup.sh
. ../contract4j5/env.sh

format=a
ext=.txt
# Run under java 6, if available, to get "jhat".
if [ $OS_NAME != "darwin" ] ; then
	export JAVA_HOME=/opt/jdk1.6
	export PATH=$JAVA_HOME/bin:$PATH
	format=b
	ext=
fi

date

for f in ../contract4j5/contract4j5.jar ../contract4j5/lib/*.jar 
do
	CLASSPATH=$CLASSPATH:$f
done
export CLASSPATH
#echo $CLASSPATH

ajc -Xms200m -Xmx500m -1.5 -source 1.5 -aspectpath ../contract4j5/contract4j5.jar -inpath bin -cp $CLASSPATH -outjar out.jar

#type_analysis="cpu=samples,interval=20,depth=5"
#type_analysis="cpu=times,interval=20,depth=10"
#type_analysis="cpu=times,interval=2,depth=20"
type_analysis="cpu=times,interval=1,depth=50"
anal=cpu.1.50
#type_analysis="heap=sites"
#anal=heap

#java -Xms200m -Xmx1000m -ea -javaagent:$ASPECTJ_HOME/lib/aspectjweaver.jar -agentlib:hprof=file=java.hprof.${anal}$ext,format=$format,$type_analysis org.contract4j5.performance.test.Person 2 "$@"
java -Xms200m -Xmx1000m -cp "$CLASSPATH:out.jar" -agentlib:hprof=file=java.hprof.${anal}$ext,format=$format,$type_analysis org.contract4j5.performance.test.Person 2 "$@"

if [ $OS_NAME != "darwin" ] ; then
	jhat.sh
fi
