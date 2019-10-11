package com.github.gmazelier.tasks

import net.sf.jasperreports.engine.DefaultJasperReportsContext
import net.sf.jasperreports.engine.JasperReportsContext
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import static net.sf.jasperreports.engine.design.JRCompiler.*
import static net.sf.jasperreports.engine.xml.JRReportSaxParserFactory.COMPILER_XML_VALIDATION

class JasperReportsPreCompile extends DefaultTask {

	@InputDirectory
	File srcDir
	@OutputDirectory
	File tmpDir
	@OutputDirectory
	File outDir
	@Input
	String srcExt
	@Input
	String outExt
	@Input
	String compiler
	@Input
	boolean keepJava
	@Input
	boolean validateXml
	@Input
	boolean verbose
	@Input
	boolean useRelativeOutDir

	@TaskAction
	void prepareCompilation() {
		checkDirectories()
		configureJasperReportsContext()
		displayConfiguration()
	}

	void checkDirectories() {
		Map<File, String> directoryErrors = [
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
			throw new InvalidUserDataException(message)
		}
	}

	void configureJasperReportsContext() {
		JasperReportsContext context = DefaultJasperReportsContext.getInstance()
		context.setProperty COMPILER_XML_VALIDATION, String.valueOf(validateXml)
		context.setProperty COMPILER_PREFIX, compiler
		context.setProperty COMPILER_KEEP_JAVA_FILE, String.valueOf(keepJava)
		context.setProperty COMPILER_TEMP_DIR, tmpDir.canonicalPath
	}

	@Internal
	def checkDirectory = { directory, isOutputDirectory ->
		// If exists, it must be a directory
		if (directory?.exists() && !directory.isDirectory())
			return [directory, "${directory} is not a directory!"]

		// If is an output directory and does not exist, create it
		if (isOutputDirectory && !directory.exists() && !directory.mkdirs())
			return [directory, "${directory} cannot be created!"]

		// If is an output directory, it must be writable
		if (isOutputDirectory && !directory.canWrite())
			return [directory, "${directory} is not writeable!"]

		[directory, null]
	}

	void displayConfiguration() {
		if (!verbose) return

		getLogger().with {
			lifecycle ">>> JasperReports Plugin Configuration"
			lifecycle "Source directory: ${srcDir.canonicalPath}"
			lifecycle "Temporary directory: ${tmpDir.canonicalPath}"
			lifecycle "Output directory: ${outDir.canonicalPath}"
			lifecycle "Source files extension: ${srcExt}"
			lifecycle "Compiled files extension: ${outExt}"
			lifecycle "Compiler: ${compiler}"
			lifecycle "Keep Java files: ${keepJava}"
			lifecycle "Validate XML before compiling: ${validateXml}"
			lifecycle "Use relative outDir: ${useRelativeOutDir}"
			lifecycle "<<<"
		}
	}
}
