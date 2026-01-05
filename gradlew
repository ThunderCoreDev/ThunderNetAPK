#!/usr/bin/env sh

##############################################################################
## Gradle start up script for UN*X
##############################################################################

DIR="$( cd "$( dirname "$0" )" && pwd )"
GRADLE_OPTS=""

exec "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"