tag=$CI_COMMIT_TAG

build_type='release'

if [[ $tag == 'uat'* ]]
then
  flavor='uat'
else
  flavor='prd'
fi

echo "tag: $tag"
echo "build_type: $build_type"
echo "flavor: $flavor"

#./gradlew "assemble$flavor$build_type"
#apkDir="app/build/outputs/apk/$flavor/$build_type"
#apkMetaFile="$apkDir/output-metadata.json"
#apkFile=$(cat $apkMetaFile | jq -r '.elements[0].outputFile')
#cp "$apkDir/$apkFile" "furniture-store.apk"
