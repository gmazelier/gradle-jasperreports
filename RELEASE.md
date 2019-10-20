# Gradle JasperReports Plugin Release Notes

These notes describe the release process currently in use.

## Releasing

Release version with `release` plugin:

    gradle clean release

Checkout the appropriate tag:

    git checkout $version

Optionally publish locally and use a test project (future release will include integration tests):

    gradle clean build publishToMavenLocal
    cd ../gradle-jasperreports-test
    gradle clean compileAllReports
    cd -

## Publishing

Referer to the the Gradle “Plugin Publishing” plugin [documentation](https://plugins.gradle.org/docs/publish-plugin) to configure Gradle (mainly API keys) and then simply use the following command:

    gradle publishPlugins
