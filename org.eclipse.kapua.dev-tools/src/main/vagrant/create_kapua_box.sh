#!/bin/bash

CURRDIR=$(pwd)
BASEDIR=$(dirname "$0")
KAPUA_BOX_TMP_DIR=$BASEDIR/kapua-box-tmp
KAPUA_BOX_NAME=trusty64/kapua-dev-box-0.1

echo 'Creating base kapua box named $KAPUA_BOX_NAME....'

vagrant box remove $KAPUA_BOX_NAME

mkdir -p $KAPUA_BOX_TMP_DIR

cp $BASEDIR/Kapua-Box-Vagrantfile $KAPUA_BOX_TMP_DIR/Vagrantfile

cd $KAPUA_BOX_TMP_DIR

vagrant up

vagrant package --output trusty64-kapua-dev-0.1.box

vagrant box add $KAPUA_BOX_NAME trusty64-kapua-dev-0.1.box

vagrant destroy --force 

rm -rf $KAPUA_BOX_TMP_DIR

cd $CURRDIR

echo '....done.'
