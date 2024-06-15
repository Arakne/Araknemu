#!/bin/bash
#
# This file is part of Araknemu.
#
# Araknemu is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Araknemu is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
#
# Copyright (c) 2017-2024 Vincent Quatrevieux
#

# ==============================================================================
# This script is used to start Araknemu from a Docker container.
# It relies on environment variables to configure the JVM and wait for MariaDB.
# If you want to run Araknemu outside of a Docker container, use directly
# `java -jar target/araknemu-xxx-alpha-jar-with-dependencies.jar`
# ==============================================================================

if [ "${DB_TYPE:-mysql}" = "mysql" ]; then
  # Wait for MariaDB to be ready
  echo -n "Waiting for MariaDB to be ready..."
  DB_STARTED=0

  for ((i=0; i < 300; i++))
  do
      nc -z $DB_HOST 3306 && DB_STARTED=1 && break
      echo -n "."
      sleep 0.1
  done

  if [ "$DB_STARTED" != 1 ]; then
      echo " [FAILED] MariaDB is not ready"
      exit 1
  else
    echo " [OK]"
  fi
fi

JAVA_MEMORY=${JAVA_MEMORY-"512m"}
JAVA_GC=${JAVA_GC:-"G1GC"}

if [ "$JAVA_GC_LOG" = "1" ]; then
  JAVA_GC_LOG="-Xlog:gc*:file=/srv/logs/gc-%t.log:tags,time,uptime,level:filecount=10,filesize=8M"
fi

if [ -n "$JAVA_MEMORY" ]; then
  JAVA_MEMORY="-Xms${JAVA_MEMORY} -Xmx${JAVA_MEMORY}"
fi

JAVA_OPTS="$JAVA_MEMORY -XX:+Use${JAVA_GC} $JAVA_GC_LOG $JAVA_OPTS"

echo "Starting Araknemu with java options: $JAVA_OPTS"

java $JAVA_OPTS -jar araknemu.jar
