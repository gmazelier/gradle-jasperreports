package com.github.gmazelier.plugins
import org.gradle.api.Project

class JasperReportsExtension {

	def File srcDir = new File('src/main/jasperreports')
	def File tmpDir = new File("${project.buildDir }/jasperreports")
	def File outDir = new File("${project.buildDir }/classes/main")
	def String srcExt = '.jrxml'
	def String outExt = '.jasper'
	def String compiler = 'net.sf.jasperreports.engine.design.JRJdtCompiler'
	def boolean keepJava = false
	def boolean validateXml = true
	def boolean verbose = false

	private Project project

	JasperReportsExtension(Project project)  {
		this.project = project
	}

}
