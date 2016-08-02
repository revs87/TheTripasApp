#!/usr/bin/env bash

CURR_PROJ_PWD_VAR='/Users/rui.vieira/repo/_github/PokeGOAPI-Java'
DEST_JAR_PWD_VAR='/Users/rui.vieira/repo/_github/TheTripasApp/app/libs/'


cd $CURR_PROJ_PWD_VAR
./gradlew clean
git pull
git submodule --init
./gradlew build
cd ..
cp $CURR_PROJ_PWD_VAR/build/libs/PokeGOAPI-Java-0.0.1-SNAPSHOT.jar $DEST_JAR_PWD_VAR
