#!/bin/bash
JAVA_TOOL_OPTIONS="-Dhttps.protocols=TLSv1.2 -Djavax.net.ssl.trustStore=cacerts -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.ssl.trustStoreType=JKS" 
./mvnw -Pprod -q -Dexec.executable=java -Dexec.classpathScope=runtime -DskipTests=true org.codehaus.mojo:exec-maven-plugin:3.0.0:exec "-Dexec.args=-classpath %classpath com.github.braully.app.SpringMainConfig"
