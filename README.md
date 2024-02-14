# Jasmine IntelliJ Plugin  [![CircleCI](https://circleci.com/gh/jasmine/IdeaJasmine/tree/main.svg?style=svg)](https://circleci.com/gh/jasmine/IdeaJasmine/tree/main)

This adds a new run configuration to IntelliJ (and IntelliJ based IDEs) that runs Jasmine and displays the results in the test console.

## Usage

### Installation
1. In your IDE preferences, go to the "Plugins" node  
    * You can install it directly from [jetbrains](https://plugins.jetbrains.com/plugin/10449-jasmine)
    * Or download from [release page](https://github.com/jasmine/IdeaJasmine/releases) and "Install plugin from disk"
    * If you really want to be on the cutting edge, you can download the build artifact from the latest build [on Circle CI](https://app.circleci.com/pipelines/github/jasmine/IdeaJasmine?branch=main)
2. Restart your IDE

### Configure
1. In the "Run" menu, click on "Edit Configurations", there should be a new "Jasmine" default configuration
1. Click the "+" to add a new configuration and select "Jasmine" as the template
1. Fill in your Node.js interpreter and select the correct Jasmine package and set any other options you want
1. Run your tests!

## Development

This plugin uses the [Gradle IntelliJ Plugin](https://github.com/JetBrains/gradle-intellij-plugin) for downloading
IntelliJ dependencies and packaging.

The code for the plugin is written in [Kotlin](http://kotlinlang.org/)

Local builds expect to be run on JDK-17, but higher might also work. Managing multiple Java versions is possible
with something like [SDKMan](https://sdkman.io/) 

### Building
Build the plugin with:  
`./gradlew build`

The distribution zip will be available at `build/distributions/IdeaJasmine-{version}.zip`

### Testing
Run IntelliJ IDEA with this plugin installed:  
`./gradlew runIde`
