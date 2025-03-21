#!/usr/bin/env bash

# Change to script directory
cd "${0%/*}"

cd ..
./gradlew clean shadowJar

cd text-ui-test

# Get today's date in YYYY-MM-DD format
TODAY=$(date +%Y-%m-%d)

# Replace <DATE> with actual date and save to EXPECTED-UNIX.TXT
sed -E "s/<DATE>/$TODAY/g" EXPECTED.TXT > EXPECTED-UNIX.TXT

# Ensure both files have Unix line endings
dos2unix EXPECTED-UNIX.TXT ACTUAL.TXT

# Find the latest jar file
JAR_FILE=$(ls -t ../build/libs/*.jar | head -n 1)

# Run the test
java -jar "$JAR_FILE" < input.txt > ACTUAL.TXT

# Compare output
diff -wB EXPECTED-UNIX.TXT ACTUAL.TXT
if [ $? -eq 0 ]
then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi
