# Jasmine IntelliJ Plugin  [![Build Status](https://travis-ci.com/jasmine/IdeaJasmine.svg?branch=main)](https://travis-ci.com/jasmine/IdeaJasmine)

This adds a new run configuration to IntelliJ (and IntelliJ based IDEs) that runs Jasmine and displays the results in the test console.

## Usage

### Installation
1. In your IDE preferences, go to the "Plugins" node  
    * You can install it directly from [jetbrains](https://plugins.jetbrains.com/plugin/10449-jasmine)
    * Or download from [release page](https://github.com/jasmine/IdeaJasmine/releases) and "Install plugin from disk"
2. Restart your IDE

### Configure
1. In the "Run" menu, click on "Edit Configurations", there should be a new "Jasmine" default configuration
1. Click the "+" to add a new configuration and select "Jasmine" as the template
1. Fill in your NodeJS interpreter and select the correct Jasmine package and set any other options you want
1. Run your tests!

## Development

This plugin uses the [Gradle IntelliJ Plugin](https://github.com/JetBrains/gradle-intellij-plugin) for downloading
IntelliJ dependencies and packaging.

The code for the plugin is written in [Kotlin](http://kotlinlang.org/)

### Building
Build the plugin with:  
`./gradlew build`

The distribution zip will be available at `build/distributions/IdeaJasmine-{version}.zip`

### Testing
Run IntelliJ IDEA with this plugin installed:  
`./gradlew runIde`
