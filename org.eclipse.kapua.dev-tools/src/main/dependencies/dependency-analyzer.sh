#!/bin/bash

PROJECT_ROOT=../../../..

cd "$PROJECT_ROOT"

mvn clean install -Dmaven.test.skip=true

mkdir -p ./target/dependencies

mvn license:aggregate-add-third-party -Dlicense.useMissingFile=true -Dlicense.fileTemplate=./src/license/third-party-file.ftl

find . -name "THIRD-PARTY.txt" -exec cat {} \; | sort -b -u | egrep -v Lists | egrep -v kapua > ./target/dependencies/eclipse-kapua_all-dependencies.csv

mvn -o dependency:list -DexcludeTransitive=true | egrep -v "Finished at:" | grep -v "kapua" | grep ":.*:.*:.*" | cut -d] -f2- | sed 's/:[a-z]*$//g;s/^ *//;s/:jar:/:/g' | sort -u > ./target/dependencies/eclipse-kapua_top-level-dependencies.csv

cd ./org.eclipse.kapua.dev-tools/src/main/dependencies
