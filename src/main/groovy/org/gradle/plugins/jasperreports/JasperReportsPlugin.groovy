package org.gradle.plugins.jasperreports

import org.gradle.api.Plugin
import org.gradle.api.Project

class JasperReportsPlugin implements Plugin<Project> {

	def extension

	@Override
	void apply(Project project) {
		project.extensions.create("jasperreports", JasperReportsExtension, project)
		extension = project.jasperreports

		project.task('prepareReportsCompilation') << {
			def Map<File,String> directoryErrors = [
					(extension.srcDir): false,
					(extension.tmpDir): true,
					(extension.outDir): true,
			].collect { directory, isOutputDirectory ->
				checkDirectory directory, isOutputDirectory
			}.collectEntries().findAll { it.value }

			if (directoryErrors) {
				def message = directoryErrors.collect { directory, errorMessage ->
					"${directory?.canonicalPath}: $errorMessage"
				}.join ', '
				throw new IllegalArgumentException(message)
			}

			displayConfiguration()
		}
	}

	def checkDirectory = { directory, isOutputDirectory ->
		// If exists, it must be a directory
		if (directory?.exists() && !directory.isDirectory())
			return [directory, "${directory} is not a directory!"]
		// If is an output directory and does not exist, create it
		if (isOutputDirectory && !directory.exists() && !directory.mkdirs())
			return [directory, "${directory} cannot be create!"]
		// If is an output directory, it must be writable
		if (isOutputDirectory && !directory.canWrite())
			return [directory, "${directory} is not writeable!"]
		[directory, null]
	}

	void displayConfiguration() {
		if (!extension.verbose) return

		println ">>> JasperReports Plugin Configuration"
		println "Source directory: ${extension.srcDir}"
		println "Temporary directory: ${extension.tmpDir}"
		println "Output directory: ${extension.outDir}"
		println "Source files extension: ${extension.srcExt}"
		println "Compiled files extension: ${extension.outExt}"
		println "Compiler: ${extension.compiler}"
		println "Keep Java files: ${extension.keepJava}"
		println "Validate XML before compiling: ${extension.validateXml}"
	}

}
