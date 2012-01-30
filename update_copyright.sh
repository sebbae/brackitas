cat LICENSE.txt | awk 'BEGIN { print "/*"} { print " * " $0} END { print " */"}' > LICENSE.tmp 
for FILE in `find ./src -iname '*.java*'`; do 
	echo -n Processing $FILE ... ;
	TMP=${FILE}.tmp;
	cat LICENSE.tmp > $TMP;
	sed -n '/^package/,$p' $FILE >> $TMP;
	#tail -n +28 $FILE >> $TMP;
	mv $TMP $FILE;
	echo DONE; 
done
rm LICENSE.tmp
