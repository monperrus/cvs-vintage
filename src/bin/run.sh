#!/bin/sh
### ====================================================================== ###
##                                                                          ##
##  JBoss Bootstrap Script                                                  ##
##                                                                          ##
### ====================================================================== ###

### $Id: run.sh,v 1.33 2002/01/04 00:09:54 user57 Exp $ ###

DIRNAME=`dirname $0`
PROGNAME=`basename $0`
GREP="grep"

#
# Helper to complain.
#
die() {
    echo "${PROGNAME}: $*"
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$JBOSS_HOME" ] &&
        JBOSS_HOME=`cygpath --unix "$JBOSS_HOME"`
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
    [ -n "$JAVAC_JAR" ] &&
        JAVAC_JAR=`cygpath --unix "$JAVAC_JAR"`
fi

# Setup JBOSS_HOME
if [ "x$JBOSS_HOME" = "x" ]; then
    # get the full path (without any relative bits)
    JBOSS_HOME=`cd $DIRNAME/..; pwd`
fi
export JBOSS_HOME

# Setup the JVM
if [ "x$JAVA_HOME" != "x" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA="java"
fi

# Setup the classpath
JBOSS_BOOT_CLASSPATH="$JBOSS_HOME/bin/run.jar"

# Include the JDK javac compiler for JSP pages. The default is for a Sun JDK
# compatible distribution which JAVA_HOME points to
if [ "x$JAVAC_JAR" = "x" ]; then
    JAVAC_JAR="$JAVA_HOME/lib/tools.jar"
fi

if [ "x$JBOSS_CLASSPATH" = "x" ]; then
    JBOSS_CLASSPATH="$JBOSS_BOOT_CLASSPATH:$JAVAC_JAR"
else
    JBOSS_CLASSPATH="$JBOSS_CLASSPATH:$JBOSS_BOOT_CLASSPATH:$JAVAC_JAR"
fi

# Check for SUN(tm) JVM w/ HotSpot support
HAS_HOTSPOT=`$JAVA -version 2>&1 | $GREP HotSpot`

# If JAVA_OPTS is not set and the JVM is HOTSPOT enabled, then the server mode
if [ "x$JAVA_OPTS" = "x" -a "x$HAS_HOTSPOT" != "x" ]; then
    JAVA_OPTS="-server"
fi

# Setup JBoss sepecific properties
JAVA_OPTS="$JAVA_OPTS -Djboss.boot.loader.name=$PROGNAME"

# Setup the JAXP parser to use
if [ "x$JAXP" = "x" ]; then
    # Default to crimson
    JAXP="crimson"
fi

case "$JAXP" in
    crimson)
	JAXP_DOM_FACTORY="org.apache.crimson.jaxp.DocumentBuilderFactoryImpl"
	JAXP_SAX_FACTORY="org.apache.crimson.jaxp.SAXParserFactoryImpl"
	;;

    xerces)
	JAXP_DOM_FACTORY="org.apache.xerces.jaxp.DocumentBuilderFactoryImpl"
	JAXP_SAX_FACTORY="org.apache.xerces.jaxp.SAXParserFactoryImpl"
	;;

    *)
	if [ "x$JAXP_DOM_FACTORY" = "x" ] &&
	   [ "x$JAXP_SAX_FACTORY" = "x" ]; then
	    die "Unsupported JAXP parser: $JAXP"
	fi
	;;
esac

JAVA_OPTS="$JAVA_OPTS -Djavax.xml.parsers.DocumentBuilderFactory=$JAXP_DOM_FACTORY"
JAVA_OPTS="$JAVA_OPTS -Djavax.xml.parsers.SAXParserFactory=$JAXP_SAX_FACTORY"

# Where we need to be to start the server
startdir="$JBOSS_HOME/bin"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    JBOSS_HOME=`cygpath --path --windows "$JBOSS_HOME"`
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
    JBOSS_CLASSPATH=`cygpath --path --windows "$JBOSS_CLASSPATH"`
fi

# Display our environment
echo "================================================================================"
echo " JBoss Bootstrap Environment"
echo ""
echo " JBOSS_HOME: $JBOSS_HOME"
echo ""
echo " JAVA: $JAVA"
echo ""
echo " JAVA_OPTS: $JAVA_OPTS"
echo ""
echo " CLASSPATH: $JBOSS_CLASSPATH"
echo ""
echo "================================================================================"
echo ""

# Make sure we are in the correctly directory
cd $startdir

# Execute the JVM
exec $JAVA \
    $JAVA_OPTS \
    -classpath $JBOSS_CLASSPATH \
    org.jboss.Main "$@"
