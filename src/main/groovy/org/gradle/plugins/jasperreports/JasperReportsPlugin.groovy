package org.gradle.plugins.jasperreports

import org.gradle.api.Plugin
import org.gradle.api.Project

class JasperReportsPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.extensions.create("jasperreports", JasperReportsExtension, project)
	}

}
