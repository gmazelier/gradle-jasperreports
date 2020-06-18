import java.net.URI
plugins {
  	id("com.gradle.plugin-publish") version "0.12.0"
  	id("net.researchgate.release") version "2.8.1"
	groovy
	`maven-publish`
	`java-gradle-plugin`
}

// Unless overridden in the pluginBundle config DSL, the project version will
// be used as your plugin version when publishing
group = "com.github.abnud1"
version = project.property("version") as String

repositories {
	jcenter()
	mavenCentral()
	maven {
		url = URI("http://jaspersoft.artifactoryonline.com/jaspersoft/third-party-ce-artifacts/")
	}
}

dependencies {
	compileOnly(gradleApi())
	compileOnly(localGroovy())
	compileOnly("net.sf.jasperreports:jasperreports:6.12.2")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

// Use java-gradle-plugin to generate plugin descriptors and specify plugin ids
gradlePlugin {
	plugins {
		create("jasperreports"){
			id = "com.github.abnud1.jasperreports"
			implementationClass = "com.github.abnud1.plugins.JasperReportsPlugin"
		}
	}
}

pluginBundle {
	// These settings are set for the whole plugin bundle
	website = "https://github.com/abnud1/gradle-jasperreports"
	vcsUrl = "https://github.com/abnud1/gradle-jasperreports.git"
	description = "Provides the capability to compile JasperReports design files."
	tags = listOf("gradle", "jasperreports")

	(plugins) {
		"jasperreports"{
			displayName = "Gradle JasperReports Plugin"
		}
	}
}

val sourcesJar = tasks.register<Jar>("sourcesJar"){
	archiveClassifier.set("sources")
	from(sourceSets.main.get().allSource)
}.get()
val groovydoc: Groovydoc by tasks
val groovydocJar = tasks.register<Jar>("groovydocJar"){
	dependsOn("groovydoc")
	archiveClassifier.set("groovydoc")
	from(groovydoc.destinationDir)
}.get()
publishing {
	// uncomment the following section to publish plugin to local maven repository using the following task:
	// $ gradle publishToMavenLocal
	/*
	repositories {
		mavenLocal()
	}
	*/
	publications {
		create<MavenPublication>("mavenJava"){
			afterEvaluate{
				artifact(sourcesJar)
				artifact(groovydocJar)
			}
		}
	}
}
tasks.withType(GroovyCompile::class){
	sourceCompatibility = "1.8"
	targetCompatibility = "1.8"
}

tasks.test{
	useJUnitPlatform()
}
artifacts {
	archives(sourcesJar)
	archives(groovydocJar)
}
