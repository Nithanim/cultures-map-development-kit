#!/bin/bash
export PATH="/mount/data/Programs/jdk-12.0.2+10/bin:$PATH"
mvn -P fat-jar clean package
cd target
jar="cultures-language-server-*-SNAPSHOT.jar"

mkdir package || true

echo "Generating minimal jvm..."
jlink --module-path /mount/data/Programs/jdk-12.0.2+10_windows/jmods --add-modules java.base,java.logging,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,jdk.management --no-header-files --no-man-pages --strip-debug --compress 2 --output package/jre
pwd
echo "Copying jar..."
cp $jar package/server.jar

cd ..

