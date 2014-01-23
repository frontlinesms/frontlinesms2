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
cp -r "grails-app/conf/help" $tempDir
tree "$tempDir"
for f in $(find $tempDir -name \*.txt); do
	echo "$f.html"
	filename=$(basename "$f")
	filenameWithoutExtension="${filename%.*}"
	echo $filenameWithoutExtension
	pandoc --from=markdown --to=html $f -o $f.html
	rm $f
	echo "\n"
done
google-chrome "$tempDir"
exit 0

