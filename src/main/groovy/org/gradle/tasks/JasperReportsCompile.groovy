package org.gradle.tasks

import static groovyx.gpars.GParsPool.withPool

import net.sf.jasperreports.engine.JasperCompileManager
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
				def File src = change.file
				def File out = new File(outDir, change.file.name.replaceAll(srcExt, outExt) as String)
				try {
					JasperCompileManager.compileReportToFile(src.absolutePath, out.absolutePath);
				} catch (any) {
					log.lifecycle "An error occured: ${any.message}", any
					return [name: src.name, success: false]
				}
				[name: src.name, success: true]
			}
		}
	}

}
