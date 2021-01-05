#!/bin/bash

setProperty() {
    awk -v pat="^$1=" -v value="$1=$2" '{ if ($0 ~ pat) print value; else print $0; }' $3 > $3.tmp
    mv $3.tmp $3
}

next_version=$1
if [[ -z $next_version ]]; then
    echo Next version is empty.
    exit 1
fi

gradle_props="./gradle.properties"
current_version=$(grep VERSION_NAME $gradle_props | cut -d'=' -f2)
release_version=${current_version%-SNAPSHOT}

echo "#Prepare releasing version $release_version."
echo "#Next version will be $next_version-SNAPSHOT."

echo setProperty VERSION_NAME "$release_version" $gradle_props
echo git commit -am \"Prepare for release "$release_version".\"
echo ./release/bintray_upload.sh
echo git tag -a "$release_version" -m \""$release_version"\"

echo setProperty VERSION_NAME "$next_version-SNAPSHOT" $gradle_props
echo git commit -am \"Prepare next development version.\"
echo git push \&\& git push --tags

if [[ $2 != "-x" ]]; then
    echo
    while true; do
        read -r -p "Do you wish to execute this (Y/n)?" yn
        case $yn in
            Y)
                eval "$("$0" "$1" -x)"
                break
                ;;
            *)
                echo exit
                exit
                ;;
        esac
    done
fi
