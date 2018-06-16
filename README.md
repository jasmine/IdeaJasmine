# Jasmine IntelliJ Plugin

This adds a new run configuration to IntelliJ (and IntelliJ based IDEs) that runs Jasmine and displays the results in the test console.

## Usage

1. Head over to the [release page](https://github.com/jasmine/IdeaJasmine/releases) and download the `IdeaJasmine-<version>.zip` for the latest release. 
1. In your IDE preferences, go to the "Plugins" node
1. Click the "Install plugin from disk" button and select the zip file you downloaded
1. Restart your IDE
1. In the "Run" menu, click on "Edit Configurations", there should be a new "Jasmine" default configuration
1. Click the "+" to add a new configuration and select "Jasmine" as the template
1. Fill in your NodeJS interpreter and select the correct Jasmine package and set any other options you want
1. Run your tests!

## Development

Follow the [JetBrains instructions](https://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/setting_up_environment.html) for setting up a development environment.

Once you've set up the IntelliJ IDEA SDK, you'll need to configure the project to use it in the projects window.

This plugin [depends](https://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_dependencies.html) on the JavaScriptLanguage support from IntelliJ.
You'll need to add the following files to the classpath of your IDEA SDK.
- `JavaScriptLanguage.jar`
- `javascript-openapi.jar`

You should be able to find both in `plugins/JavaScriptLanguage/lib` inside your IntelliJ install directory.

The `package.sh` script relies on the build output being written to the `out` directory in the project.

The code for the plugin is written in [Kotlin](http://kotlinlang.org/)

### Testing

You'll need to build the project with IntelliJ and then run the `package.sh` script to create `IdeaJasmine.zip`.
You can then use the "Install plugin from disk" feature in the IntelliJ IDE of your choice. 