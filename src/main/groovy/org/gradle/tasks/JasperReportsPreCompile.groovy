package org.gradle.tasks
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class JasperReportsPreCompile extends DefaultTask {

	def File srcDir
	def File tmpDir
	def File outDir
	def String srcExt
	def String outExt
	def String compiler
	def boolean keepJava
	def boolean validateXml
	def boolean verbose

	@TaskAction
	void checkConfiguration() {
		def Map<File,String> directoryErrors = [
				(srcDir): false,
				(tmpDir): true,
				(outDir): true,
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
		if (!verbose) return

		getLogger().with {
			lifecycle ">>> JasperReports Plugin Configuration"
			lifecycle "Source directory: ${srcDir}"
			lifecycle "Temporary directory: ${tmpDir}"
			lifecycle "Output directory: ${outDir}"
			lifecycle "Source files extension: ${srcExt}"
			lifecycle "Compiled files extension: ${outExt}"
			lifecycle "Compiler: ${compiler}"
			lifecycle "Keep Java files: ${keepJava}"
			lifecycle "Validate XML before compiling: ${validateXml}"
		}
	}

}
