package org.gradle.plugins.jasperreports

import org.gradle.api.Plugin
import org.gradle.api.Project

class JasperReportsPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.extensions.create("jasperreports", JasperReportsExtension, project)

		project.task('prepareReportsCompilation') << {
			def Map<File,String> directoryErrors = [
					(project.jasperreports.srcDir): false,
					(project.jasperreports.tmpDir): true,
					(project.jasperreports.outDir): true,
			].collect { directory, isOutputDirectory ->
				checkDirectory directory, isOutputDirectory
			}.collectEntries().findAll { it.value }

			if (directoryErrors) {
				def message = directoryErrors.collect { directory, errorMessage ->
					"${directory?.canonicalPath}: $errorMessage"
				}.join ', '
				throw new IllegalArgumentException(message)
			}
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

}
