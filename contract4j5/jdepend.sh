#!/bin/bash

java jdepend.xmlui.JDepend -file jdepend_report.xml "$@"
xsltproc /cygdrive/c/tools/javatools/jdepend-2.9.1/contrib/jdepend2dot.xsl jdepend_report.xml > jdepend_report.dot
/cygdrive/c/Program\ Files/ATT/Graphviz/bin/dot -Tpng jdepend_report.dot -o jdepend_report.png
/cygdrive/c/Program\ Files/Mozilla\ Firefox/firefox.exe jdepend_report.png &
