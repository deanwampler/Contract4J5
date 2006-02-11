#!/bin/bash
#------------------------------------------
# build.sh - Simple build driver script
#
# Edit to suit your environment or define these variables other ways.
# Note that they are used by the ant build (except for TOOL_HOME, which
# is used for convenience).
#
# Notes:
#  1) This currently has problems on Windows, running under cygwin. I
#     don't think the conversion to/from windows path formats is correct
#     (TBD). However, the build.bat script works fine.
#
#  Copyright 2005 Dean Wampler. All rights reserved.
#  http://www.aspectprogramming.com
# 
#  Licensed under the Eclipse Public License - v 1.0; you may not use this
#  software except in compliance with the License. You may obtain a copy of the 
#  License at
# 
#      http://www.eclipse.org/legal/epl-v10.html
# 
#  A copy is also included with this distribution. See the "LICENSE" file.
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
# 
#  @author deanwampler <dean@aspectprogramming.com>

VERSION=1.5.0
#set -x

osname=`uname -s 2> /dev/null | /usr/bin/tr "[:upper:]" "[:lower:]" 2> /dev/null`
echo "Building on $osname"

case "$osname" in
    cygwin*)
		: ${TOOL_HOME:=/cygdrive/c/tools/javatools}
		;;
	*)
		: ${TOOL_HOME:=$HOME/tools}
		;;
esac

: ${CONTRACT4J5_HOME:=$HOME/src/java/contract4j5_0_5_0/contract4j5}
: ${ANT_HOME:=$TOOL_HOME/Apache/apache-ant-1.6.2}
: ${ASPECTJ_HOME:=$TOOL_HOME/aspectj1.5}
: ${JAVA_HOME:=$TOOL_HOME/jdk$VERSION}
: ${JUNIT_HOME:=$TOOL_HOME/junit3.8.1}
: ${JEXL_HOME:=$TOOL_HOME/Apache/jakarta/commons}

case "$osname" in
    cygwin*)
	#PATH=`cygpath --windows --path "$PATH"`
	#PATH="$JAVA_HOME\\bin;$ASPECTJ_HOME\\bin;$ANT_HOME\\bin;$PATH"
	PATH="$JAVA_HOME/bin:$ASPECTJ_HOME/bin:$ANT_HOME/bin:$PATH"
	HOME=`cygpath --windows --path "$HOME"`
	TOOL_HOME=c:/tools/javatools
	CONTRACT4J5_HOME=`cygpath --windows --path "$CONTRACT4J5_HOME"`
	ANT_HOME=`cygpath --windows --path "$ANT_HOME"`
	ASPECTJ_HOME=`cygpath --windows --path "$ASPECTJ_HOME"`
	JAVA_HOME=`cygpath --windows --path "$JAVA_HOME"`
	JUNIT_HOME=`cygpath --windows --path "$JUNIT_HOME"`
	JEXL_HOME=`cygpath --windows --path "$JEXL_HOME"`
	CLASSPATH=`cygpath --windows --path "$CLASSPATH"`
	CLASSPATH="$ANT_HOME\\lib\\ant.jar;$ASPECTJ_HOME\\lib\\aspectjrt.jar;$ASPECTJ_HOME\\lib\\aspectjtools.jar;$JUNIT_HOME\\junit.jar;$JEXL_HOME\\commons-jexl-1.0.jar;$CLASSPATH"
	;;
    *)
	CLASSPATH=$ANT_HOME/lib/ant.jar:$ASPECTJ_HOME/lib/aspectjrt.jar:$ASPECTJ_HOME/lib/aspectjtools.jar:$JUNIT_HOME/junit.jar:$CLASSPATH
	PATH=$JAVA_HOME/bin:$ASPECTJ_HOME/bin:$ANT_HOME/bin:$PATH
	;;
esac

export ANT_HOME
export ASPECTJ_HOME
export CONTRACT4J5_HOME
export JAVA_HOME
export JUNIT_HOME
export JEXL_HOME
export TOOL_HOME
export CLASSPATH
export PATH

ant "$@"
