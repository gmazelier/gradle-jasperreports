package com.github.gmazelier.plugins

import com.github.gmazelier.tasks.JasperReportsCompile
import com.github.gmazelier.tasks.JasperReportsPreCompile
import org.gradle.api.Plugin
import org.gradle.api.Project

class JasperReportsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("jasperreports", JasperReportsExtension, project)
        def extension = project.jasperreports as JasperReportsExtension

        def preCompileTask = project.tasks.register('prepareReportsCompilation', JasperReportsPreCompile) {
            description = 'Configure JasperReports compiler and environment.'
            group = 'jasperReports'
        }.get()

        def compileTask = project.tasks.register('compileAllReports', JasperReportsCompile) {
            description = 'Compile JasperReports design source files.'
            group = 'jasperReports'
            dependsOn = ['prepareReportsCompilation']
        }.get()

        project.afterEvaluate {
            // Precompile task
            preCompileTask.srcDir.set(project.file(extension.srcDir.absolutePath))
            preCompileTask.tmpDir = extension.tmpDir
            preCompileTask.outDir = extension.outDir
            preCompileTask.srcExt = extension.srcExt
            preCompileTask.outExt = extension.outExt
            preCompileTask.compiler = extension.compiler
            preCompileTask.keepJava = extension.keepJava
            preCompileTask.validateXml = extension.validateXml
            preCompileTask.verbose = extension.verbose
            preCompileTask.useRelativeOutDir = extension.useRelativeOutDir
            // Compile task
            compileTask.classpath = project.jasperreports.classpath
            compileTask.srcDir.set(project.file(extension.srcDir.absolutePath))
            compileTask.outDir = project.jasperreports.outDir
            compileTask.srcExt = project.jasperreports.srcExt
            compileTask.outExt = project.jasperreports.outExt
            compileTask.verbose = project.jasperreports.verbose
            compileTask.useRelativeOutDir = project.jasperreports.useRelativeOutDir
        }
    }
}