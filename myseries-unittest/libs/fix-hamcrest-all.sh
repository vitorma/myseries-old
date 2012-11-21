#!/bin/sh
# Copyright 2012 Gabriel Assis Bezerra <gabriel.bezerra@gmail.com>

HELP_MESSAGE="\
Usage: $0 <original-hamcrest-jar> <destination-jar>\n\
\n\
hamcrest-all comes with a set of tools in the package org.hamcrest.generator\n\
that relies on reflection to work. Android tools complain about what is in this\n\
package.\n\
\n\
This script removes that package from hamcrest-all-x.x.jar generating a new jar\n\
to be used in the Android test project.\n\
\n\
Author: Gabriel Assis Bezerra <gabriel.bezerra@gmail.com>"

if [ $# -ne 2 ]; then
    echo $HELP_MESSAGE
    exit 1
fi

ORIGINAL_HAMCREST_FILE=$1
OUTPUT_HAMCREST_FILE=$2

TEMP_HAMCREST_DIR="/tmp/hamcrest-fix"

# to avoid zip to update the file
rm $OUTPUT_HAMCREST_FILE

unzip $ORIGINAL_HAMCREST_FILE -d $TEMP_HAMCREST_DIR
  DIRECTORY_WHERE_THIS_SCRIPT_RUNS_FROM=$PWD
  cd $TEMP_HAMCREST_DIR
    # remove org.hamcrest.generator package from the file
    rm -r org/hamcrest/generator

    # repacking into OUTPUT_HAMCREST_FILE
    zip -r $DIRECTORY_WHERE_THIS_SCRIPT_RUNS_FROM/$OUTPUT_HAMCREST_FILE .

  cd $DIRECTORY_WHERE_THIS_SCRIPT_RUNS_FROM
rm -r $TEMP_HAMCREST_DIR  # clean up

