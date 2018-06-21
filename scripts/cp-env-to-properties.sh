#!/usr/bin/env bash
#
# Copy env variables to app module gradle properties file
#

# Reliably include our config file
DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi
. "$DIR/config.sh"



set +x // dont print the next lines on run script
mkdir ~/.gradle
printenv | tr ' ' '\n' | grep $ENV_VAR_PREFIX > ~/.gradle/gradle.properties
set -x
