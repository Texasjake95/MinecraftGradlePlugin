#!/usr/bin/env bash

# delete the directory form last time in case of failure
rm -rf maven-repo

# pull the maven repo from git
git clone https://Texasjake95@github.com/Texasjake95/maven-repo.git ./maven-repo -v
