#!/bin/bash

KAPUA_BOX_TMP_DIR=./kapua-box-tmp
KAPUA_BOX_NAME=trusty64/kapua-dev-box-0.1

vagrant box remove "$KAPUA_BOX_NAME"

mkdir -p "$KAPUA_BOX_TMP_DIR"

cp Kapua-Box-Vagrantfile "$KAPUA_BOX_TMP_DIR"/Vagrantfile

cd "$KAPUA_BOX_TMP_DIR"

vagrant up

vagrant package --output trusty64-kapua-dev-0.1.box

vagrant box add "$KAPUA_BOX_NAME" trusty64-kapua-dev-0.1.box

vagrant destroy

cd ..

rm -rf "$KAPUA_BOX_TMP_DIR"
