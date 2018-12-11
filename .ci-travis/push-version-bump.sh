#!/bin/bash

BUILD_VERSION=$1
GIT_USER=$2
GIT_PWD=$3

GIT_PROJECT_NAME="central-deployment"
GIT_REPO_URL="https://$GIT_USER:$GIT_PWD@github.com/alv-ch/$GIT_PROJECT_NAME.git"

VERSION_FILE="group_vars/dev/versions.yml"
VERSION_STRING=candidate_service_version

echo "Using project version: $BUILD_VERSION..."
echo "Using git user email: $GIT_USER..."
echo "Using git user password: $GIT_PWD..."

echo "Cloning the $GIT_PROJECT_NAME repository..."
git clone $GIT_REPO_URL

# replace the version
cd $GIT_PROJECT_NAME
sed -i "s/$VERSION_STRING.*/$VERSION_STRING\: $BUILD_VERSION/" $VERSION_FILE

# push changes
echo "Pushing the version $BUILD_VERSION to the $GIT_PROJECT_NAME repository..."
git commit -m "$VERSION_STRING bump to $BUILD_VERSION" $VERSION_FILE
git push
