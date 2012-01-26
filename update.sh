#!/bin/bash
echo "Updating projects: $@"

for project in $@; do
	echo "Building $project..."
	pushd $project && mvn clean install && popd
done

echo "Done updating projects: $@"
