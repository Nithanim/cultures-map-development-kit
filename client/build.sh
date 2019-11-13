#!/bin/bash
set -e

if [ ! -f ".vscodeignore" ]; then
	echo "Wrong directory!"
	exit 1
fi

echo "=> Removing old server"
rm -rf server
echo "=> Copying current server build from other project"

if [ ! -d "../server/target/package" ]; then
	echo "Server project not built!"
	exit 1
fi

cp -r ../server/target/package server

echo "=> Building extension file"
vsce package
