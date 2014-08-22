# Gradle JasperReports Plugin

## Description

Provides the capability to compile JasperReports design files. This plugin is designed to work like the Maven plugins [Maven 2 JasperReports Plugin](http://mojo.codehaus.org/jasperreports-maven-plugin/) and [JasperReports-plugin](https://github.com/alexnederlof/Jasper-report-maven-plugin).

## Usage

### Installation

### Configuration

Below are the parameters that can be used to configure the build:

| Parameter     | Type      | Description                                                                                   |
|---------------|-----------|-----------------------------------------------------------------------------------------------|
| `srcDir`      | `File`    | Design source files directory. Default value: `src/main/jasperreports`                        |
| `tmpDir`      | `File`    | Temporary files (`.java`) directory. Default value: `${project.buildDir}/jasperreports`       |
| `outDir`      | `File`    | Compiled reports file directory. Default value: `${project.buildDir}/classes/main`            |
| `srcExt`      | `String`  | Design source files extension. Default value: `'.jrxml'`                                      |
| `outExt`      | `String`  | Compiled reports files extension. Default value: `'.jasper'`                                  |
| `compiler`    | `String`  | The report compiler to use. Default value: `net.sf.jasperreports.engine.design.JRJdtCompiler` |
| `keepJava`    | `boolean` | Keep temporary files after compiling. Default value: `false`                                  |
| `validateXml` | `boolean` | Validate source files before compiling. Default value: `true`                                 |
| `verbose`     | `boolean` | Verbose plugin outpout. Default value: `false`                                                |

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
    }

### Custom Build Process

Adding a task dependency is very simple. For example, if you want to make sure that Groovy (and Java) compilation is successfully performed before JasperReports designs compilation, just add the following to your build script:

    compileAllReports.dependsOn compileGroovy

## Getting Help

To ask questions or report bugs, please use the [Github project](https://github.com/gmazelier/gradle/jasperreports/issues).

## License
This plugin is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
without warranties or conditions of any kind, either express or implied.
