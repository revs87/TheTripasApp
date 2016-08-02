#!/bin/bash 

cd ..
current_dir=$(pwd)
current_api='PokeGOAPI-Java'
current_proj='TheTripasApp/app/libs/'

cd $current_api
./gradlew clean
git pull
git submodule --init
./gradlew build
cd ..
cp $current_api/build/libs/PokeGOAPI-Java-0.0.1-SNAPSHOT.jar $current_proj
