 #!/bin/bash

# Reliably include our config file
DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi
. "$DIR/config.sh"

function create_github_release { 

    version=$2
    
    echo "Uploading release"
    
    response=`curl -X POST -H "Content-Type:application/json" -H "Authorization: token $GITHUB_RELEASE_TOKEN" -d '{"tag_name": "'$version'","name": "'$version'","body": "'"$GITHUB_RELEASE_DESC"'","draft": false}' $GITHUB_RELEASE_URL`
    
    echo "Got response $response"
    
    id=`echo $response | python -c "import json,sys;obj=json.load(sys.stdin);print obj['id'];"`
    
    if [ -z "$id" ]; then
        return 1
    fi
    
    echo "Found id $id"
    
    # Upload apk file

    curl -H "Content-Type:application/zip" -H "Authorization: token $GITHUB_RELEASE_TOKEN" --data-binary @"$GITHUB_RELEASE_FILE_PATH" $GITHUB_UPLOAD_URL$id/assets?name=$GITHUB_RELASE_FILENAME

    return
}

# Only deploy releases if we are on the master branch
# if [[ $GIT_CURRENT_BRANCH != "master" ]]; then
#     echo "Not on master branch, so not deploying release"
#     exit 0
# fi

# This will push a github release every time a new tag is pushed
# you should ensure tags are push with commits by doing the following:
# git config --global push.followTags true

if [[ $GIT_TAG != *"undefined"* ]]; then
    echo "Creating github release for tag $GIT_TAG"
    if create_github_release $GITHUB_RELEASE_MODULE $GIT_TAG; then
        webhook $GITHUB_RELEASE_MODULE "$GITHUB_RELEASE_NAME" "Created github release for tag $TAG"
    else
        webhook $GITHUB_RELEASE_MODULE "$GITHUB_RELEASE_NAME" "Failed to create github release for tag $TAG :("
    fi
else
    echo "Not releasing as no new tag detected"
fi
