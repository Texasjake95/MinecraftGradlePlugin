#!/usr/bin/env bash

# run gradle
./gradlew uploadArchives -Pfilesmaven="file:maven-repo/"

# change to the maven repo directory
cd ./maven-repo

# add correct origin to the repo 
git remote rm origin
git remote add origin https://Texasjake95:${GH_TOKEN}@github.com/Texasjake95/maven-repo.git

# configure repo
git config user.email "texasjake95@gmail.com"
git config user.name "Texasjake95"

# configure git push
git config push.default current

# preform commit
git add .
git commit -q -m "Travis-CI Build Push"

# push commit
git push -q
