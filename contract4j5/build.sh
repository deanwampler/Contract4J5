#!/bin/bash
#------------------------------------------
# build.sh - Simple build driver *nix shell script
#
# See "env.sh" for environment variable definitions.
#
# Notes:
#  1) This currently has problems on Windows, running under cygwin. I
#     don't think the conversion to/from windows path formats is correct
#     (TBD). However, the build.bat script works fine.
#
#  Copyright 2005, 2006 Dean Wampler. All rights reserved.
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

. ./env.sh

ant "$@"
