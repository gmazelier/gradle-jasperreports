# Gradle JasperReports Plugin

## Description

## Configuration

| Parameter     | Type      | Default                                            | Description                             |
|---------------|-----------|----------------------------------------------------|-----------------------------------------|
| `srcDir`      | `File`    | `src/main/jasperreports`                           | Design source files directory.          |
| `tmpDir`      | `File`    | `${project.buildDir}/jasperreports`                | Temporary files (`.java`) directory.    |
| `outDir`      | `File`    | `${project.buildDir}/classes/main`                 | Compiled reports file directory.        |
| `srcExt`      | `String`  | `'.jrxml'`                                         | Design source files extension.          |
| `outExt`      | `String`  | `'.jasper'`                                        | Compiled reports files extension.       |
| `compiler`    | `String`  | `net.sf.jasperreports.engine.design.JRJdtCompiler` | The report compiler to use.             |
| `keepJava`    | `boolean` | `false`                                            | Keep temporary files after compiling.   |
| `validateXml` | `boolean` | `true`                                             | Validate source files before compiling. |
| `verbose`     | `boolean` | `false`                                            | Verbose plugin outpout.                 |

## Example

## Installation

## Getting Help

To ask questions or report bugs, please use the [Github project](https://github.com/gmazelier/gradle/jasperreports/issues).

## License
This plugin is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
without warranties or conditions of any kind, either express or implied.
