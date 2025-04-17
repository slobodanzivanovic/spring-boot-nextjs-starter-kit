#!/bin/sh
set -e

JAVA_OPTS="-Xms512m -Xmx1g"

DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

exec java ${JAVA_OPTS} ${DEBUG_OPTS} -jar /app/app.jar "$@"
