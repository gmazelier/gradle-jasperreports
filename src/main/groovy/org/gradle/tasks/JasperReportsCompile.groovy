package org.gradle.tasks

import static groovyx.gpars.GParsPool.withPool

import net.sf.jasperreports.engine.JasperCompileManager
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

class JasperReportsCompile extends DefaultTask {

	@InputDirectory def File srcDir
	@OutputDirectory def File outDir
	@Input def String srcExt
	@Input def String outExt
	def boolean verbose

	@TaskAction
	void execute(IncrementalTaskInputs inputs) {
		def log = getLogger()

		def compilationTasks = []
		inputs.outOfDate { change ->
			if (change.file.name.endsWith(srcExt))
				compilationTasks << [src: change.file, out: outputFile(change.file)]
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
				def File src = task['src'] as File
				def File out = task['out'] as File
				if (verbose) log.lifecycle "Compiling file ${src.name}"
				try {
					JasperCompileManager.compileReportToFile src.absolutePath, out.absolutePath
				} catch (any) {
					return [name: src.name, success: false, exception: any]
				}
				[name: src.name, success: true]
			}
		}

		def stop = System.currentTimeMillis()

		def failures = results.findAll { !it['success'] }
		if (failures) throw new GradleException(failureMessage(failures))

		logger.lifecycle "${results.size()} designs compiled in ${stop - start} ms"
	}

	def File outputFile(File src) {
		new File(outDir, src.name.replaceAll(srcExt, outExt))
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
