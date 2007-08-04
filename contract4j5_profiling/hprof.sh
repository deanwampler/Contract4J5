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

cpu=samples  # times
interval=1
depth=500
type_analysis="cpu=$cpu,interval=$interval,depth=$depth"
anal=cpu.$interval.$depth
#type_analysis="heap=sites"
#anal=heap

file=java.hprof.${anal}$ext
java -Xms200m -Xmx1000m -cp "out.jar:$CLASSPATH" -agentlib:hprof=file=$file,format=$format,$type_analysis org.contract4j5.performance.test.Person 10 "$@"

if [ $OS_NAME != "darwin" ] ; then
  echo "Running jhat.sh..."
  echo ""
	echo "Open your browser to localhost:7000"
	jhat.sh $file
fi
