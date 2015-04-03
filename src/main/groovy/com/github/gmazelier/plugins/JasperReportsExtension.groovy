package com.github.gmazelier.plugins

import org.gradle.api.Project

class JasperReportsExtension {

	Iterable<File> classpath = []
	File srcDir = new File('src/main/jasperreports')
	File tmpDir = new File("${project.buildDir}/jasperreports")
	File outDir = new File("${project.buildDir}/classes/main")
	String srcExt = '.jrxml'
	String outExt = '.jasper'
	String compiler = 'net.sf.jasperreports.engine.design.JRJdtCompiler'
	boolean keepJava = false
	boolean validateXml = true
	boolean verbose = false
	boolean useRelativeOutDir = false

	private Project project

	JasperReportsExtension(Project project) {
		this.project = project
	}

}
