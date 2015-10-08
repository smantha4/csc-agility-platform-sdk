#!/bin/sh

# default tomcat installation
if [ z"$CATALINA_HOME" == "z" ]; then
   CATALINA_HOME=/usr/local/tomcat
fi

WEBINF=$CATALINA_HOME/webapps/agility/WEB-INF
CLASSPATH=$WEBINF/classes:$WEBINF/bundles/com.servicemesh.agility.api-1.0.0.jar
for file in $WEBINF/lib/*.jar
do
   CLASSPATH=$CLASSPATH:$file
done

java -Dcatalina.home="$CATALINA_HOME" -cp $CLASSPATH -Xms512m -Xmx2048m -XX:MaxPermSize=256m com.servicemesh.agility.internal.api.v1_0.Import $@
