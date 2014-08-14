package org.gradle.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

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

			displayConfiguration project.getLogger()
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

	void displayConfiguration(Logger log) {
		if (!extension.verbose) return

		log.with {
			lifecycle ">>> JasperReports Plugin Configuration"
			lifecycle "Source directory: ${extension.srcDir}"
			lifecycle "Temporary directory: ${extension.tmpDir}"
			lifecycle "Output directory: ${extension.outDir}"
			lifecycle "Source files extension: ${extension.srcExt}"
			lifecycle "Compiled files extension: ${extension.outExt}"
			lifecycle "Compiler: ${extension.compiler}"
			lifecycle "Keep Java files: ${extension.keepJava}"
			lifecycle "Validate XML before compiling: ${extension.validateXml}"
		}
	}

}
