#/bin/bash

generateZip=false

OPTIND=1

while getopts "z:" opt; do
	case $opt in
		z) generateZip=$OPTARG
			;;
	esac
done

tempDir="`mktemp -d`"
cp -r grails-app/conf/help/* $tempDir
cp -r web-app/* $tempDir
for f in $(find $tempDir -name \*.txt); do
	filename=$(basename "$f")
	filenameWithoutExtension="${filename%.*}"
	pandoc --from=markdown --to=html $f -o $f.html
	rm $f
done

for f in $(ls web-app | grep images --invert-match); do
	rm -rf "$tempDir/$f"
done

for f in $(ls web-app/images | grep help --invert-match); do
	rm -rf "$tempDir/images/$f"
done

google-chrome "$tempDir"
exit 0

