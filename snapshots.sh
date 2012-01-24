#!/bin/bash

echo "Clearing snapshot directories..."

dirs=( $( < 'snapshots.list') )
for dir in $dirs
do
	echo "Removing snapshot directory: $dir"
	rm -rf ~/.ivy2/cache/$dir
done

echo "Calling 'grails $@'"
grails $@

