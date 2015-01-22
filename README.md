# Gradle JasperReports Plugin

Latest version: [![download latest version](https://api.bintray.com/packages/gmazelier/maven/com.github.gmazelier:jasperreports-gradle-plugin/images/download.png)](https://bintray.com/gmazelier/maven/com.github.gmazelier:jasperreports-gradle-plugin/_latestVersion)

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

To use in Gradle 2.1 and later...

    plugins {
        id 'com.github.gmazelier.jasperreports' version '0.1.0'
    }

To use in earlier versions...

    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath 'com.github.gmazelier:jasperreports-gradle-plugin:0.1.0'
        }
    }

    apply plugin: 'com.github.gmazelier.jasperreports'

### Configuration

Below are the parameters that can be used to configure the build:

| Parameter     | Type             | Description                                                                                                    |
|---------------|------------------|----------------------------------------------------------------------------------------------------------------|
| `srcDir`      | `File`           | Design source files directory. Default value: `src/main/jasperreports`                                         |
| `tmpDir`      | `File`           | Temporary files (`.java`) directory. Default value: `${project.buildDir}/jasperreports`                        |
| `outDir`      | `File`           | Compiled reports file directory. Default value: `${project.buildDir}/classes/main`                             |
| `srcExt`      | `String`         | Design source files extension. Default value: `'.jrxml'`                                                       |
| `outExt`      | `String`         | Compiled reports files extension. Default value: `'.jasper'`                                                   |
| `compiler`    | `String`         | The report compiler to use. Default value: `net.sf.jasperreports.engine.design.JRJdtCompiler`                  |
| `keepJava`    | `boolean`        | Keep temporary files after compiling. Default value: `false`                                                   |
| `validateXml` | `boolean`        | Validate source files before compiling. Default value: `true`                                                  |
| `verbose`     | `boolean`        | Verbose plugin outpout. Default value: `false`                                                                 |
| `classpath`   | `Iterable<File>` | Extra elements to add to the classpath when compile. Default value: `project.sourceSets.main.runtimeClasspath` |

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
        classpath = project.sourceSets.main.runtimeClasspath
    }

### Custom Build Process

Adding a task dependency is very simple. For example, if you want to make sure that Groovy (and Java) compilation is successfully performed before JasperReports designs compilation, just add the following to your build script:

    compileAllReports.dependsOn compileGroovy

## Getting Help

To ask questions or report bugs, please use the [Github project](https://github.com/gmazelier/gradle/jasperreports/issues).

## License
This plugin is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
without warranties or conditions of any kind, either express or implied.
