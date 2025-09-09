#!/bin/bash

# Find all Java files in the controller directory
CONTROLLERS=$(find Backend/src/main/java/in/osop/messaging_platform/controller -name "*.java")

# Loop through each controller file
for file in $CONTROLLERS; do
  echo "Processing $file"
  
  # Remove @CrossOrigin annotations
  sed -i 's/@CrossOrigin(origins = "http:\/\/localhost:3000")//g' "$file"
  sed -i 's/@CrossOrigin(origins = "\*")//g' "$file"
  sed -i 's/@CrossOrigin(origins = "\*") \/\/ Allow requests from React app//g' "$file"
  
  # Clean up any empty lines created by the removal
  sed -i '/^$/d' "$file"
done

echo "Completed removing @CrossOrigin annotations from controllers"
