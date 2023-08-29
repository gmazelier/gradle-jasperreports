package com.github.gmazelier.tasks

import java.nio.file.Path
import java.nio.file.Paths
import net.sf.jasperreports.engine.JasperCompileManager
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

import static groovyx.gpars.GParsPool.withPool

class JasperReportsCompile extends DefaultTask {

    @InputFiles
    Iterable<File> classpath
    @Incremental
    @InputDirectory
    final DirectoryProperty srcDir = project.objects.directoryProperty().convention(project.layout.projectDirectory.dir('src/main/jasper'))
    @OutputDirectory
    File outDir
    @Input
    String srcExt
    @Input
    String outExt
    @Input
    boolean verbose
    @Input
    boolean useRelativeOutDir
    @Console
    def log = getLogger()

    @TaskAction
    void execute(InputChanges inputs) {

        def dependencies = classpath.collect { dependency ->
            dependency?.toURI()?.toURL()
        }
        if (verbose) log.lifecycle "Additional classpath: ${dependencies}"

        def compilationTasks = []
        inputs.getFileChanges(srcDir).each { change ->
            switch (change.changeType) {
                case ChangeType.REMOVED:
                    if (verbose) {
                        log.lifecycle "Removed file ${change.file.absolutePath}"
                    }
                    def fileToRemove = outputFile(change.file)
                    fileToRemove.delete()
                    break
                default:
                    // added or modified
                    if (change.file.name.endsWith(srcExt)) {
                        compilationTasks << [src: change.file, out: outputFile(change.file), deps: dependencies]
                    }
                    break
            }
        }

        def start = System.currentTimeMillis()

        def results = []
        withPool {
            results = compilationTasks.collectParallel { task ->
                def src = task['src'] as File
                def out = task['out'] as File
                def deps = task['deps']

                if (verbose) log.lifecycle "Compiling file ${src.absolutePath}"

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

    File outputFile(File src) {
        // local variable so we aren't continuously appending to the outDir
        def useOutDir = outDir

        if (useRelativeOutDir) {

            Path srcPath = src.toPath()
            Path srcDirPath = srcDir.getAsFile().get().getAbsoluteFile().toPath()
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

    @Console
    def failureMessage = { List failures ->
        def stringBuilder = new StringBuilder()
        stringBuilder.append "Could not compile ${failures.size()} designs:\n"
        failures.each { failure ->
            stringBuilder.append "\t[${failure['name']}] ${failure['exception'].message}\n"
        }
        stringBuilder.toString()
    }

}
