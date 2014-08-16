package org.gradle.plugins
import org.gradle.api.Project
import org.gradle.tasks.JasperReportsCompile
import org.gradle.tasks.JasperReportsPreCompile
import org.gradle.testfixtures.ProjectBuilder

class JasperReportsPluginTest extends GroovyTestCase {

	public void testPluginAddsJasperReportsPreCompileTask() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'jasperreports'

		assert project.tasks.prepareReportsCompilation instanceof JasperReportsPreCompile
	}

	public void testPluginAddsJasperReportsCompileTask() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'jasperreports'

		assert project.tasks.compileAllReports instanceof JasperReportsCompile
	}

	public void testCompileAllReportsDependsOnPrepareReportsCompilation() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'jasperreports'

		assert project.tasks.compileAllReports.dependsOn(project.tasks.prepareReportsCompilation)
	}

	public void testPluginAddsJasperReportsExtension() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'jasperreports'

		assert project.jasperreports instanceof JasperReportsExtension
	}

	public void testPluginHasDefaultValues() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'jasperreports'

		def jasperreports = project.jasperreports as JasperReportsExtension
		assert jasperreports.srcDir == new File('src/main/jasperreports')
		assert jasperreports.tmpDir == new File("${project.buildDir }/jasperreports")
		assert jasperreports.outDir == new File("${project.buildDir }/classes/main")
		assert jasperreports.srcExt == '.jrxml'
		assert jasperreports.outExt == '.jasper'
		assert jasperreports.compiler == 'net.sf.jasperreports.engine.design.JRJdtCompiler'
		assert !jasperreports.keepJava
		assert jasperreports.validateXml
		assert !jasperreports.verbose
	}

}
