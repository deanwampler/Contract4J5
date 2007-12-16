#!/bin/bash
#------------------------------------------
# env.sh - Sets up environment for *nix shell builds.
#
# Edit to suit your environment or define these variables other ways.
# Note that they are used by the ant build (except for TOOL_HOME, which
# is used for convenience).
#
# Notes:
#  1) This currently has problems on Windows, running under cygwin. I
#     don't think the conversion to/from windows path formats is correct
#     (TBD). Try the build.bat script.
# 
#  @author deanwampler <dean@aspectprogramming.com>

JDK_VERSION=1.5.0
#set -x

osname=`uname -s 2> /dev/null | /usr/bin/tr "[:upper:]" "[:lower:]" 2> /dev/null`
echo "Building on $osname"

case "$osname" in
	cygwin*)
		: ${TOOLS_HOME:=/cygdrive/c/tools/}
		;;
	darwin*)
		: ${TOOLS_HOME:=/Library/tools}
		;;
	linux*)
		: ${TOOLS_HOME:=/opt/tools}
		;;
esac

# Ignore the definitions for components that you don't use.

: ${CRUISECONTROLRB_HOME:=$TOOLS_HOME/cruisecontrolrb-1.1.0}  # If you use CC.rb...
: ${JAVA_TOOLS_HOME:=$TOOLS_HOME/java}
: ${CONTRACT4J5_ROOT:=$HOME/src/java/contract4j5_080/trunk}
: ${CONTRACT4J5_HOME:=$CONTRACT4J5_ROOT/contract4j5}
: ${CONTRACT4J5_LIB:=$CONTRACT4J5_HOME/lib}
: ${ANT_HOME:=$TOOLS_HOME/Apache/apache-ant-1.6.5}
: ${ASPECTJ_HOME:=$TOOLS_HOME/aspectj1.5}
: ${JAVA_HOME:=$TOOLS_HOME/jdk$JDK_VERSION}
: ${JUNIT_HOME:=$TOOLS_HOME/junit3.8.1}
: ${SPRING_HOME:=$TOOLS_HOME/Spring/spring-framework-1.2.5}

case "$osname" in
	cygwin*)
	PATH="$JAVA_HOME/bin:$ASPECTJ_HOME/bin:$ANT_HOME/bin:$PATH"
	HOME=`cygpath --windows --path "$HOME"`
	TOOLS_HOME=`cygpath --windows --path "$TOOLS_HOME"`
	CRUISECONTROLRB_HOME=`cygpath --windows --path "$CRUISECONTROLRB_HOME"`
	JAVA_TOOLS_HOME=`cygpath --windows --path "$JAVA_TOOLS_HOME"`
	CONTRACT4J5_ROOT=`cygpath --windows --path "$CONTRACT4J5_ROOT"`
	CONTRACT4J5_HOME=`cygpath --windows --path "$CONTRACT4J5_HOME"`
	CONTRACT4J5_LIB=`cygpath --windows --path "$CONTRACT4J5_LIB"`
	ANT_HOME=`cygpath --windows --path "$ANT_HOME"`
	ASPECTJ_HOME=`cygpath --windows --path "$ASPECTJ_HOME"`
	JAVA_HOME=`cygpath --windows --path "$JAVA_HOME"`
	JUNIT_HOME=`cygpath --windows --path "$JUNIT_HOME"`
	SPRING_HOME=`cygpath --windows --path "$SPRING_HOME"`
	CLASSPATH=`cygpath --windows --path "$CLASSPATH"`
	CLASSPATH="$ANT_HOME\\lib\\ant.jar;$ASPECTJ_HOME\\lib\\aspectjrt.jar;$ASPECTJ_HOME\\lib\\aspectjtools.jar;$JUNIT_HOME\\junit.jar;$CLASSPATH"
	;;
	*)
	PATH=$JAVA_HOME/bin:$ASPECTJ_HOME/bin:$ANT_HOME/bin:$PATH
	CLASSPATH=$ANT_HOME/lib/ant.jar:$ASPECTJ_HOME/lib/aspectjrt.jar:$ASPECTJ_HOME/lib/aspectjtools.jar:$JUNIT_HOME/junit.jar:$CLASSPATH
	;;
esac

export TOOLS_HOME
export JAVA_TOOLS_HOME
export ANT_HOME
export ASPECTJ_HOME
export CONTRACT4J5_HOME
export CONTRACT4J5_LIB
export JAVA_HOME
export JUNIT_HOME
export SPRING_HOME
export TOOLS_HOME
export CLASSPATH
export PATH
