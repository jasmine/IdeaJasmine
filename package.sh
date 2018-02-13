#!/usr/bin/env bash

# compile the jar
rm -rf .packaging
rm IdeaJasmine.zip
mkdir -p .packaging/IdeaJasmine/lib
mkdir -p .packaging/META-INF
echo "Manifest-Version: 1.0" > .packaging/META-INF/MANIFEST.MF

jar -cf .packaging/IdeaJasmine/lib/IdeaJasmine.jar -C out/production/IdeaJasmine io -C out/production/IdeaJasmine icons -C resources META-INF -C .packaging META-INF

# zip it up
cp resources/intellij_reporter.js .packaging/IdeaJasmine/lib/
cd .packaging
zip -qr ../IdeaJasmine.zip IdeaJasmine/