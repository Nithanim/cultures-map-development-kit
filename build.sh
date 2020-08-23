#!/bin/bash

cd cultures-format-cif
mvn clean install
cd ..

cd cultures-format-lib
mvn clean install
cd ..

cd server
mvn clean install
cd native
mvn clean install -Pfat-jar
cd ..
cd ..

#cd ../client
#source build.sh
#cd ..
