#!/bin/bash
# RUN THIS SCRIPT FROM ROOT PROJECT DIR

# Remove deploy dir if exists
rm -rf SMART-MIRROR

# Create new deploy dir
mkdir SMART-MIRROR

# Copy scripts into deploy dir
cp -a scripts/ SMART-MIRROR/

# Create resource dir for config file
mkdir SMART-MIRROR/resources
cp src/main/resources/mirror_config.xml SMART-MIRROR/resources

# Copy web service dir into deploy dir
cp -r ./web-service ./SMART-MIRROR

# Copy jar into deploy dir
cp out/smart-mirror.jar SMART-MIRROR/

echo 'Deploy directory created on:' >> SMART-MIRROR/info.txt &
date >> SMART-MIRROR/info.txt

echo "Success!"
