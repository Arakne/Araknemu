#!/bin/bash

function isInterface() {
	className=$(basename $1)
	fileName="src/main/java/$1.java"

	if grep -q "interface $className" "$fileName"
	then
		return 0
	fi

	return 1
}

function isEnum() {
	className=$(basename $1)
	fileName="src/main/java/$1.java"

	if grep -q "enum $className" "$fileName"
	then
		return 0
	fi

	return 1
}

function isEntity() {
	className=$(basename $1)
	fileName="src/main/java/$1.java"

	if grep -q "package fr.quatrevieux.araknemu.data.*.entity.*;" "$fileName"
	then
		return 0
	fi

	return 1
}

SRC=$(find src/main -name '*.java' | sed s/src.main.java.// | sed s/.java// | sort) 
TEST=$(find src/test -name '*.java' | sed s/src.test.java.// | sed s/Test.java// | sort)

FILES=()
NB_FILES=0
NB_UNCOVERED=0

for file in $SRC
do
	found=0
	((NB_FILES++))

	for file2 in $TEST
	do
		if [ $file = $file2 ]
		then
			found=1
			break
		fi
	done

	if ((found != 1))
	then
		FILES+=("$file")
	fi
done

for file in ${FILES[@]}
do
	if isInterface $file || isEnum $file || isEntity $file
	then
		((NB_FILES--))
		continue
	fi

	if [[ "$file" == *Exception ]]
	then
		((NB_FILES--))
		continue
	fi

	((NB_UNCOVERED++))
	echo $file
done

echo "Non couvert : $NB_UNCOVERED / $NB_FILES ($((100 - (NB_FILES-NB_UNCOVERED) * 100 / NB_FILES))%)"
