# Gradle JasperReports Plugin

## Description

## Configuration

| Parameter     | Type      | Default                                            | Description |
|---------------|-----------|----------------------------------------------------|-------------|
| `srcDir`      | `File`    | `src/main/jasperreports`                           |             |
| `tmpDir`      | `File`    | `${project.buildDir}/jasperreports`                |             |
| `outDir`      | `File`    | `${project.buildDir}/classes/main`                 |             |
| `srcExt`      | `String`  | `'.jrxml'`                                         |             |
| `outExt`      | `String`  | `'.jasper'`                                        |             |
| `compiler`    | `String`  | `net.sf.jasperreports.engine.design.JRJdtCompiler` |             |
| `keepJava`    | `boolean` | `false`                                            |             |
| `validateXml` | `boolean` | `true`                                             |             |
| `verbose`     | `boolean` | `false`                                            |             |

## Example

## Installation

## Getting Help

To ask questions or report bugs, please use the [Github project](https://github.com/gmazelier/gradle/jasperreports/issues).

## License
This plugin is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
without warranties or conditions of any kind, either express or implied.
