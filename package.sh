#!/usr/bin/env bash

# cleanup
rm -rf .packaging
rm -f IdeaJasmine.zip

# compile the jar
mkdir -p .packaging/IdeaJasmine/lib

jar -cf .packaging/IdeaJasmine/lib/IdeaJasmine.jar \
    -C out/production/IdeaJasmine io \
    -C out/production/IdeaJasmine icons \
    -C resources META-INF

# zip it up
cp resources/intellij_reporter.js .packaging/IdeaJasmine/lib/
cd .packaging
zip -qr ../IdeaJasmine.zip IdeaJasmine/