ps auxww | grep run.jar | grep -v grep | head -1 | awk " { split(\$0,bob,\" \*\"); print \"kill -9 \", bob[2] }" | sh

if [ -d "$TOMCAT_HOME" -a -d "$SERVLETAPI_HOME" ] ; then 
   CLASSPATH=$CLASSPATH:$TOMCAT_HOME/classes
   CLASSPATH=$CLASSPATH:$SERVLETAPI_HOME/lib/servlet.jar
fi

CLASSPATH=$CLASSPATH:run.jar

java -classpath $CLASSPATH -Dtomcat.home=$TOMCAT_HOME org.jboss.Main
