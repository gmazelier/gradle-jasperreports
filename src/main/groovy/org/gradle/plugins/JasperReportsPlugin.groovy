package org.gradle.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.tasks.JasperReportsPreCompile

class JasperReportsPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.extensions.create("jasperreports", JasperReportsExtension, project)
		def extension = project.jasperreports as JasperReportsExtension

		project.task('prepareReportsCompilation', type: JasperReportsPreCompile) {
			srcDir = extension.srcDir
			tmpDir = extension.tmpDir
			outDir = extension.outDir
			srcExt = extension.srcExt
			outExt = extension.outExt
			compiler = extension.compiler
			keepJava = extension.keepJava
			validateXml = extension.validateXml
			verbose = extension.verbose
		}
	}

}
