package com.github.gmazelier.tasks

import net.sf.jasperreports.engine.JasperCompileManager
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.*
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

import java.nio.file.Path
import java.nio.file.Paths

import static groovyx.gpars.GParsPool.withPool

class JasperReportsCompile extends DefaultTask {

	@InputFiles
	Iterable<File> classpath
	@InputDirectory
	def File srcDir
	@OutputDirectory
	def File outDir
	@Input
	def String srcExt
	@Input
	def String outExt
	def boolean verbose
	def boolean useRelativeOutDir
	def log = getLogger()

	@TaskAction
	void execute(IncrementalTaskInputs inputs) {

		def dependencies = classpath.collect { dependency ->
			dependency?.toURI()?.toURL()
		}
		if (verbose) log.lifecycle "Additional classpath: ${dependencies}"

		def compilationTasks = []
		inputs.outOfDate { change ->
			if (change.file.name.endsWith(srcExt))
				compilationTasks << [src: change.file, out: outputFile(change.file), deps: dependencies]
		}
		inputs.removed { change ->
			if (verbose) log.lifecycle "Removed file ${change.file.name}"
			def fileToRemove = outputFile(change.file)
			fileToRemove.delete()
		}

		def start = System.currentTimeMillis()

		def results = []
		withPool {
			results = compilationTasks.collectParallel { task ->
				def src = task['src'] as File
				def out = task['out'] as File
				def deps = task['deps']

				if (verbose) log.lifecycle "Compiling file ${src.name}"

				try {
					// Configure class loader with addtional dependencies
					ClassLoader originalLoader = Thread.currentThread().getContextClassLoader()
					URLClassLoader loader = new URLClassLoader(deps as URL[], originalLoader)
					Thread.currentThread().setContextClassLoader loader
					// Compile report
					JasperCompileManager.compileReportToFile src.absolutePath, out.absolutePath
					// Restore class loader
					Thread.currentThread().setContextClassLoader originalLoader
				} catch (any) {
					return [name: src.name, success: false, exception: any]
				}
				[name: src.name, success: true]
			}
		}

		def stop = System.currentTimeMillis()

		def failures = results.findAll { !it['success'] }
		if (failures) throw new GradleException(failureMessage(failures))

		log.lifecycle "${results.size()} designs compiled in ${stop - start} ms"
	}

	def File outputFile(File src) {

		// local variable so we aren't continuously appending to the outDir
		def useOutDir = outDir

		if (useRelativeOutDir) {

			Path srcPath = src.toPath()
			Path srcDirPath = srcDir.getAbsoluteFile().toPath()
			Path relativePath = srcDirPath.relativize(srcPath)
			def parent = relativePath.parent != null ? relativePath.parent.toString() : ""
			def path = Paths.get(useOutDir.absolutePath, parent)

			useOutDir = path.toFile()
			if (!useOutDir.isDirectory()) {
				if (verbose) log.lifecycle "Create outDir: ${useOutDir.absolutePath}"
				useOutDir.mkdirs()
			}
		}

		new File(useOutDir, src.name.replaceAll(srcExt, outExt))

	}

	def failureMessage = { List failures ->
		def stringBuilder = new StringBuilder()
		stringBuilder.append "Could not compile ${failures.size()} designs:\n"
		failures.each { failure ->
			stringBuilder.append "\t[${failure['name']}] ${failure['exception'].message}\n"
		}
		stringBuilder.toString()
	}

}