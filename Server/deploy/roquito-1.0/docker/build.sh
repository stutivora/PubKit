#! /bin/bash -e

rm -rf build
mkdir build
cp ../target/roquito-1.0.jar build

docker build -t roquito .