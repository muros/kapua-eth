#!/bin/bash

PROJECT_ROOT=../..

mkdir -p "$PROJECT_ROOT"/target/dependencies

mvn -f "$PROJECT_ROOT"/pom.xml license:aggregate-add-third-party -Dlicense.useMissingFile=true -Dlicense.fileTemplate="$PROJECT_ROOT"/src/license/third-party-file.ftl

find "$PROJECT_ROOT" -name "THIRD-PARTY.txt" -exec cat {} \; | sort -b -u | egrep -v Lists | egrep -v kapua > "$PROJECT_ROOT"/target/dependencies/eclipse-kapua_all-dependencies.csv

mvn -o dependency:list -DexcludeTransitive=true | egrep -v "Finished at:" | grep -v "kapua" | grep ":.*:.*:.*" | cut -d] -f2- | sed 's/:[a-z]*$//g;s/^ *//;s/:jar:/:/g' | sort -u > "$PROJECT_ROOT"/target/dependencies/eclipse-kapua_top-level-dependencies.csv
