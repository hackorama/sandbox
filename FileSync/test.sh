#!/bin/bash

#
# Basic file sync test util.
#
# Builds and runs the app and tests initial and incremental chekcs 
# by running the app in the background.
# 
# Sync is cross checked using "diff -r"
#
# TODO: Add more test cases
# TODO: Test multiple intervals 
# TODO: Add more functions and random test case generation
#

function check() {
  echo ""
  diff -r $1 $2
  [ $(diff -r $1 $2 | wc -l) == "0" ] && echo "Checking $1 <-> $2 : PASS"
  echo ""
}

# Setup source 

rm -rf /tmp/fsync
mkdir /tmp/fsync
mkdir /tmp/fsync/src
mkdir /tmp/fsync/dst


mkdir /tmp/fsync/src/1
mkdir /tmp/fsync/src/2
mkdir /tmp/fsync/src/2/21
mkdir /tmp/fsync/src/2/21/22
mkdir /tmp/fsync/src/3
mkdir /tmp/fsync/src/3/1
mkdir /tmp/fsync/src/3/31
mkdir /tmp/fsync/src/3/31/32
mkdir /tmp/fsync/src/3/31/32/33

echo "0" > /tmp/fsync/src/0.txt
echo "11" > /tmp/fsync/src/1/11.txt
echo "21" > /tmp/fsync/src/2/21/21.txt
echo "33" > /tmp/fsync/src/3/31/32/33/33.txt


# Build and run in the background

javac FileSync.java
java FileSync /tmp/fsync/src /tmp/fsync/dst 2000 &

echo ""
echo "Testing initial sync ..." 
echo ""

# Wait for app 
sleep 2

check /tmp/fsync/src /tmp/fsync/dst

echo ""
echo "Testing incremental sync ..."
echo ""

# modify file
echo "33 33" > /tmp/fsync/src/3/31/32/33/33.txt
# add file
echo "333" > /tmp/fsync/src/3/31/32/33/333.txt
# remove file 
rm /tmp/fsync/src/2/21/21.txt
# remove folder 
rm -rf /tmp/fsync/src/2/21
# add folder 
mkdir /tmp/fsync/src/2/222

# Wait for polling interval to pass
sleep 3

pkill -f 'java.*FileSync'

check /tmp/fsync/src /tmp/fsync/dst


