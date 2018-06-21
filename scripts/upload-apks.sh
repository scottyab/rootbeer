#!/bin/bash

# Reliably include our config file
DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi
. "$DIR/config.sh"

# Uploads a build to Beta
function upload_to_beta {
    echo "Uploading $1 to Beta"
    
    if ./gradlew :$1:crashlyticsUploadDistributionRelease ; then
        webhook $1 "$APP_RELEASE_NAME" "Uploading to Beta Succeeded"
    else
        webhook $1 "$APP_RELEASE_NAME" "Uploading to Beta Play FAILED :("
    fi
}

# Uploads a build to Google Play
function upload_to_google_play {
    echo "Uploading $1 to Google Play"
    
    if ./gradlew :$1:publishApkRelease ; then
        webhook $1 "$APP_RELEASE_NAME" "Uploading to Google Play Succeeded"
    else
        webhook $1 "$APP_RELEASE_NAME" "Uploading to Google Play FAILED :("
    fi
}

# # Only deploy releases if we are on the master branch
# if [[ $GIT_CURRENT_BRANCH != "master" ]]; then
#     echo "Not on master branch, so not deploying release"
#     exit 0
# fi


# Print the git commit message
echo "Git commit message: ${GIT_COMMIT_DESC}"

if [[ $GIT_COMMIT_DESC == *"#PLAY_BETA"* ]]; then
    upload_to_google_play "app"  
else
    echo "Not publishing to Google Play as deploy not found in commit message"
fi
