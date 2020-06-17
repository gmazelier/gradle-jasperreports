package com.github.gmazelier.plugins

import com.github.gmazelier.tasks.JasperReportsCompile
import com.github.gmazelier.tasks.JasperReportsPreCompile
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class JasperReportsPluginTest {

	@Test
	static void testPluginAddsJasperReportsPreCompileTask() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'
		assert project.tasks.prepareReportsCompilation instanceof JasperReportsPreCompile
    }

	@Test
	static void testPluginAddsJasperReportsCompileTask() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'

		assert project.tasks.compileAllReports instanceof JasperReportsCompile
	}
	@Test
	static void testCompileAllReportsDependsOnPrepareReportsCompilation() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'

		assert project.tasks.compileAllReports.dependsOn(project.tasks.prepareReportsCompilation)
	}

	@Test
	static void testPluginAddsJasperReportsExtension() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'

		assert project.jasperreports instanceof JasperReportsExtension
	}

	@Test
	static void testPluginHasDefaultValues() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'

		def jasperreports = project.jasperreports as JasperReportsExtension
		assert jasperreports.classpath == []
		assert jasperreports.srcDir == new File('src/main/jasperreports')
		assert jasperreports.tmpDir == new File("${project.buildDir}/jasperreports")
		assert jasperreports.outDir == new File("${project.buildDir}/classes/main")
		assert jasperreports.srcExt == '.jrxml'
		assert jasperreports.outExt == '.jasper'
		assert jasperreports.compiler == 'net.sf.jasperreports.engine.design.JRJdtCompiler'
		assert !jasperreports.keepJava
		assert jasperreports.validateXml
		assert !jasperreports.verbose
		assert !jasperreports.useRelativeOutDir
	}

	@Test
	void testPluginSpreadsDirOptions() {
		File src = new File('src/jasperreports')
		File tmp = new File('tmp/jasperreports')
		File out = new File('out/jasperreports')
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'
		project.jasperreports {
			srcDir = src
			tmpDir = tmp
			outDir = out
		}
		project.evaluate()

		assert src == project.jasperreports.srcDir
		assert src == project.tasks.prepareReportsCompilation.srcDir
		assert src == project.tasks.compileAllReports.srcDir

		assert tmp == project.jasperreports.tmpDir
		assert tmp == project.tasks.prepareReportsCompilation.tmpDir

		assert out == project.jasperreports.outDir
		assert out == project.tasks.prepareReportsCompilation.outDir
		assert out == project.tasks.compileAllReports.outDir
	}

	@Test
	void testPluginSpreadsExtOptions() {
		String src = '.xml'
		String out = '.class'
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'
		project.jasperreports {
			srcExt = src
			outExt = out
		}
		project.evaluate()

		assert src == project.jasperreports.srcExt
		assert src == project.tasks.prepareReportsCompilation.srcExt
		assert src == project.tasks.compileAllReports.srcExt

		assert out == project.jasperreports.outExt
		assert out == project.tasks.prepareReportsCompilation.outExt
		assert out == project.tasks.compileAllReports.outExt
	}

	@Test
	void testPluginSpreadsClasspathOption() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'groovy'
		project.apply plugin: 'com.github.gmazelier.jasperreports'
		project.jasperreports {
			classpath = project.sourceSets.main.output
		}
		project.evaluate()

		assert project.sourceSets.main.output == project.jasperreports.classpath
		assert project.sourceSets.main.output == project.tasks.compileAllReports.classpath
	}

	@Test
	void testPluginSpreadsCompilerOption() {
		String groovyCompiler = 'net.sf.jasperreports.compilers.JRGroovyCompiler'
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'
		project.jasperreports {
			compiler = groovyCompiler
		}
		project.evaluate()

		assert groovyCompiler == project.jasperreports.compiler
		assert groovyCompiler == project.tasks.prepareReportsCompilation.compiler
	}

	@Test
	void testPluginSpreadsKeepJavaOption() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'
		project.jasperreports {
			keepJava = true
		}
		project.evaluate()

		assert project.jasperreports.keepJava
		assert project.tasks.prepareReportsCompilation.keepJava
	}

	@Test
	void testPluginSpreadsValidateXmlOption() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'
		project.jasperreports {
			validateXml = false
		}
		project.evaluate()

		assert !project.jasperreports.validateXml
		assert !project.tasks.prepareReportsCompilation.validateXml
	}

	@Test
	void testPluginSpreadsVerboseOption() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'
		project.jasperreports {
			verbose = true
		}
		project.evaluate()

		assert project.jasperreports.verbose
		assert project.tasks.prepareReportsCompilation.verbose
		assert project.tasks.compileAllReports.verbose
	}

	@Test
	void testPluginSpreadsUseRelativeOutDirOption() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'com.github.gmazelier.jasperreports'
		project.jasperreports {
			useRelativeOutDir = true
		}
		project.evaluate()

		assert project.jasperreports.useRelativeOutDir
		assert project.tasks.prepareReportsCompilation.useRelativeOutDir
		assert project.tasks.compileAllReports.useRelativeOutDir
	}
}
