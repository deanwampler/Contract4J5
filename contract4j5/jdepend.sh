#!/bin/bash
#--------------------------------------------------------------------------
# jdepend.sh - Run JDepend and graph the results
# usage:
#   jdepend.sh --swing [bin/class directories or jar files]
# where the bin or class directories or jar files defaults to 
# "contract4j5.jar", if not specified. (So we exclude the test code, by
# default.)
# Without the "--swing" argument, it generates a png graph of dependencies
# and attempts to view it in an image viewer on your system, e.g., "gimp", 
# "oeg", or a web browser, Firefox or Konquerer. On the Mac, it uses Preview.
# Use the "--swing" option to view the text-oriented Swing browser instead.
# However, this output isn't as easy to understand.
#
# Copyright 2006 Dean Wampler. All rights reserved.
# http://www.aspectprogramming.com
#
# This script is free software; use at your own risk.

#set -x

bak=jdepend.bak

test -d $bak || mkdir $bak

date=`date +%Y%m%d.%H%M%S`

f=jdepend_report
for e in dot png xml
do
	test -f $f.$e && cp $f.$e $bak/$f.$date.$e
done

echo $CLASSPATH | grep -iq jdepend
if [ $? = 1 ] ; then
		if [ ! -d "$JDEPEND_HOME" ] ; then
				echo "JDepend must be installed and the jar must be on the CLASSPATH"
				echo "or JDEPEND_HOME must be a defined environment variable that"
				echo "points to the installation directory."
				exit 1
		fi
		jdepend_jar=`ls $JDEPEND_HOME/lib/*.jar`
		OS_NAME=`uname -s 2> /dev/null | /usr/bin/tr "[:upper:]" "[:lower:]" 2> /dev/null`
		case "$OS_NAME" in
				cygwin*)
						jdepend_jar=`cygpath --windows $jdepend_jar`
						export CLASSPATH="$CLASSPATH;$jdepend_jar"
						;;
				*)
						export CLASSPATH="$CLASSPATH:$jdepend_jar"
						;;
		esac
fi

# Try to guess an image viewer
IMAGE_VIEWER=firefox 
case "$OS_NAME" in
		darwin*)
				IMAGE_VIEWER="open -a /Applications/Preview.app"
				;;
		linux*)
				for app in eog gimp konquerer
				do
					if [ "$(which $app)" != "" ] ; then
							IMAGE_VIEWER="$app"
							break
					fi
				done
				;;
esac
#echo "using $IMAGE_VIEWER"

rm -f jdepend_report.{xml,dot,png}

use_swing=''
if [ $# -gt 0 -a "$1" = "--swing" ] ; then
		shift
		use_swing='y'
fi

if [ "$use_swing" = 'y' ] ; then
		java jdepend.swingui.JDepend . &
		exit 0
fi

DOT=`which dot`
if [ $? = 1 ] ; then
		DOT="$GRAPHVIZ_HOME/bin/dot"
		if [ ! -x "$DOT" = "" ] ; then
				echo "Graphviz must be installed and either 'dot' must be on the path."
				echo "or GRAPHVIZ_HOME must be a defined environment variable that"
				echo "points to the installation directory."
				exit 1
		fi
fi

args=("$@")
if [ $# -eq 0 ] ; then
		args=(contract4j5.jar)
fi
echo "Examining: ${args[@]}"
java jdepend.xmlui.JDepend -file jdepend_report.xml "${args[@]}"
test $? = 1  && exit 1
xsltproc "$JDEPEND_HOME/contrib/jdepend2dot.xsl" jdepend_report.xml > jdepend_report.dot
test $? = 1  && exit 1
"$DOT" -Tpng jdepend_report.dot -o jdepend_report.png
test $? = 1  && exit 1
$IMAGE_VIEWER jdepend_report.png &
ls -l jdepend_report.{xml,dot,png}
