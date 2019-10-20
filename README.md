# Gradle JasperReports Plugin

[![Build Status](https://travis-ci.org/gmazelier/gradle-jasperreports.svg)](https://travis-ci.org/gmazelier/gradle-jasperreports)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com/github/gmazelier/jasperreports-gradle-plugin/maven-metadata.xml.svg?colorB=007ec6&label=version)](https://plugins.gradle.org/plugin/com.github.gmazelier.jasperreports)

## Description

Provides the capability to compile JasperReports design files. This plugin is designed to work like the Maven plugins [Maven 2 JasperReports Plugin](http://mojo.codehaus.org/jasperreports-maven-plugin) and [JasperReports-plugin](https://github.com/alexnederlof/Jasper-report-maven-plugin). Much of this was inspired by these two projects.

## Usage

This plugin provides one main task, `compileAllReports`. It uses [incremental task](http://www.gradle.org/docs/current/dsl/org.gradle.api.tasks.incremental.IncrementalTaskInputs.html) feature to process out-of-date files and [parallel collections](http://gpars.codehaus.org/GParsPool) from [GPars](http://gpars.codehaus.org) for parallel processing. Adapt your build process to your own needs by defining the proper tasks depedencies (see *Custom Build Process* below).

If your designs compilation needs to run after Groovy compilation, running `compileAllReports` should give a similar output:

    $ gradle compileAllReports
    :compileJava UP-TO-DATE
    :compileGroovy UP-TO-DATE
    :prepareReportsCompilation
    :compileAllReports
    21 designs compiled in 2222 ms

    BUILD SUCCESSFUL

    Total time: 6.577 secs

To clean up and start fresh, simply run:

    $ gradle clean compileAllReports

### Installation

Using the pluging DSL...

    plugins {
      id "com.github.gmazelier.jasperreports" version "0.4"
    }

Using the legacy plugin application...

    buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "com.github.gmazelier:jasperreports-gradle-plugin:0.4"
      }
    }

    apply plugin: "com.github.gmazelier.jasperreports"

### Configuration

Below are the parameters that can be used to configure the build:

| Parameter     | Type             | Description                                                                                   |
|---------------|------------------|-----------------------------------------------------------------------------------------------|
| `srcDir`      | `File`           | Design source files directory. Default value: `src/main/jasperreports`                        |
| `tmpDir`      | `File`           | Temporary files (`.java`) directory. Default value: `${project.buildDir}/jasperreports`       |
| `outDir`      | `File`           | Compiled reports file directory. Default value: `${project.buildDir}/classes/main`            |
| `srcExt`      | `String`         | Design source files extension. Default value: `'.jrxml'`                                      |
| `outExt`      | `String`         | Compiled reports files extension. Default value: `'.jasper'`                                  |
| `compiler`    | `String`         | The report compiler to use. Default value: `net.sf.jasperreports.engine.design.JRJdtCompiler` |
| `keepJava`    | `boolean`        | Keep temporary files after compiling. Default value: `false`                                  |
| `validateXml` | `boolean`        | Validate source files before compiling. Default value: `true`                                 |
| `verbose`     | `boolean`        | Verbose plugin outpout. Default value: `false`                                                |
| `useRelativeOutDir`     | `boolean`        | The outDir is relative to java classpath. Default value: `false`                                                |
| `classpath`   | `Iterable<File>` | Extra elements to add to the classpath when compile. Default value: `[]`                      |

### Example

Below is a complete example, with default values:

    jasperreports {
        srcDir = file('src/main/jasperreports')
        tmpDir = file('${project.buildDir}/jasperreports')
        outDir = file('${project.buildDir}/classes/main')
        srcExt = '.jrxml'
        outExt = '.jasper'
        compiler = 'net.sf.jasperreports.engine.design.JRJdtCompiler'
        keepJava = false
        validateXml = true
        verbose = false
        useRelativeOutDir = false
        classpath = []
    }

### Custom Build Process

Adding a task dependency is very simple. For example, if you want to make sure that Groovy (and Java) compilation is successfully performed before JasperReports designs compilation, just add the following to your build script:

    compileAllReports.dependsOn compileGroovy

### Custom Classpath

#### Sharing dependencies

Here's a way to share dependencies (`joda-time` in this example) between the main project and the designs compilation:

    buildscript {
      ext {
        libs = [
          jrdeps: [
            // all dependencies shared with JasperReports
            'joda-time:joda-time:2.7'
          ]
        ]
      }
      repositories {
        jcenter()
        mavenCentral()
        maven {
            url 'http://jaspersoft.artifactoryonline.com/jaspersoft/third-party-ce-artifacts/'
        }
        maven {
          url 'http://jasperreports.sourceforge.net/maven2'
        }
        maven {
          url 'http://repository.jboss.org/maven2/'
        }
      }
      dependencies {
        classpath 'com.github.gmazelier:jasperreports-gradle-plugin:0.3.2'
        classpath libs.jrdeps
      }
    }

    apply plugin: 'groovy'
    apply plugin: 'com.github.gmazelier.jasperreports'

    repositories {
        mavenCentral()
    }

    dependencies {
      compile libs.jrdeps
    }

    jasperreports {
      verbose = true
    }

    compileAllReports.dependsOn compileGroovy

#### Adding Project Compiled Sources

Use the `classpath` property to acces your compiled sources in you JasperReports designs. Configure your build script in a similar way:

    jasperreports {
        verbose = true
        classpath = project.sourceSets.main.output
    }

## Getting Help

To ask questions or report bugs, please use the [Github project](https://github.com/gmazelier/gradle/jasperreports/issues).

## Contributors

Patches are welcome. Thanks to:

* [Blake Jackson](https://github.com/blaketastic2)
* [Rankec](https://github.com/rankec)
* [tkofford](https://github.com/tkofford)

## Change Log

### 0.4 (2019-10-20)

* Dependencies upgrade (Gradle and Jasper).
* Move to Gradle publishing plugin.

### 0.3.2 (2015-12-07)

* Adds Microsoft OS support.

### 0.3.1 (2015-11-24)

* Fix an issue if there are multiple files in subdirectories when using `useRelativeOutDir`.

### 0.3.0 (2015-11-17)

* Adds Java 8 support.
* Configures Travis CI.
* Improves tests.

### 0.2.1 (2015-04-03)

* Adds `useRelativeOutDir` option.
* Enable Gradle wrapper for developers.

### 0.2.0 (2015-02-26)

* Adds `classpath` option.

### 0.1.0 (2014-08-24)

* Initial release.

## License
This plugin is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
without warranties or conditions of any kind, either express or implied.
