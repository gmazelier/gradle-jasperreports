package org.gradle.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.tasks.JasperReportsPreCompile

class JasperReportsPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.extensions.create("jasperreports", JasperReportsExtension, project)
		def extension = project.jasperreports as JasperReportsExtension

		def preCompileTask = project.task(
				'prepareReportsCompilation',
				type: JasperReportsPreCompile
		) as JasperReportsPreCompile

		project.afterEvaluate {
			// Precompile task
			preCompileTask.srcDir = extension.srcDir
			preCompileTask.tmpDir = extension.tmpDir
			preCompileTask.outDir = extension.outDir
			preCompileTask.srcExt = extension.srcExt
			preCompileTask.outExt = extension.outExt
			preCompileTask.compiler = extension.compiler
			preCompileTask.keepJava = extension.keepJava
			preCompileTask.validateXml = extension.validateXml
			preCompileTask.verbose = extension.verbose
		}
	}

}
