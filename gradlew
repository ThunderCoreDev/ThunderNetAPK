#!/usr/bin/env sh

##############################################################################
## Gradle start up script for UN*X
##############################################################################

APP_HOME=$(cd "$(dirname "$0")"; pwd)

# Locate Java
if [ -z "$JAVA_HOME" ]; then
  JAVA_CMD=$(which java)
else
  JAVA_CMD="$JAVA_HOME/bin/java"
fi

# Gradle Wrapper JAR
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Execute Gradle
exec "$JAVA_CMD" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"