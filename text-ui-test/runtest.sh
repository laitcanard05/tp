#!/usr/bin/env bash

# Change to script directory
cd "${0%/*}"

cd ..
./gradlew clean shadowJar

cd text-ui-test

# Run the program
java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT

# Get today's date in YYYY-MM-DD format
TODAY=$(date +%Y-%m-%d)

# Replace <DATE> with today's date in EXPECTED.TXT
sed -E "s/<DATE>/$TODAY/g" EXPECTED.TXT > EXPECTED-UNIX.TXT

# Convert line endings
dos2unix EXPECTED-UNIX.TXT ACTUAL.TXT

# Compare the output
diff -wB EXPECTED-UNIX.TXT ACTUAL.TXT
if [ $? -eq 0 ]
then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi
