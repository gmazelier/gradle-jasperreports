package org.gradle.tasks

import static groovyx.gpars.GParsPool.withPool

import org.gradle.api.DefaultTask
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

		def reportsToCompile = []
		inputs.outOfDate { change ->
			reportsToCompile << change
		}
		inputs.removed { change ->
			if (verbose) log.lifecycle "Removed file ${change.file.name}"
			def fileToRemove = new File(outDir, change.file.name.replaceAll(srcExt, outExt))
			fileToRemove.delete()
		}

		def results = []
		withPool {
			results = reportsToCompile.collectParallel { change ->
				if (verbose) log.lifecycle "Compiling file ${change.file.name}"
				// TODO Design compilation
				def fileToCreate = new File(outDir, change.file.name.replaceAll(srcExt, outExt))
				fileToCreate.text = "compiled"
				// END
				[name: change.file.name, success: true]
			}
		}
	}

}
