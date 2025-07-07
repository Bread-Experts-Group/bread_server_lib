import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

plugins {
	kotlin("jvm") version "2.2.0"
	id("org.jetbrains.dokka") version "2.0.0"
	id("org.jetbrains.dokka-javadoc") version "2.0.0"
	`maven-publish`
	`java-library`
	signing
}

group = "org.bread_experts_group"
version = "3.1.0"

repositories {
	mavenCentral()
}
dependencies {
	testImplementation(kotlin("test"))
}
tasks.test {
	useJUnitPlatform()
	maxHeapSize = "20G"
}
kotlin {
	jvmToolchain(21)
	compilerOptions {
		freeCompilerArgs.add("-Xcontext-parameters")
	}
}
tasks.register<Jar>("dokkaJavadocJar") {
	dependsOn(tasks.dokkaGeneratePublicationJavadoc)
	from(tasks.dokkaGeneratePublicationJavadoc.flatMap { it.outputDirectory })
	archiveClassifier.set("javadoc")
}
val localProperties: Properties = Properties().apply {
	rootProject.file("local.properties").reader().use(::load)
}
publishing {
	publications {
		create<MavenPublication>("mavenKotlin") {
			artifactId = "$artifactId-code"
			from(components["kotlin"])
			artifact(tasks.kotlinSourcesJar)
			artifact(tasks["dokkaJavadocJar"])
			pom {
				name = "Bread Server Library"
				description = "Distribution of software for Bread Experts Group operated servers."
				url = "https://breadexperts.group"
				signing {
					sign(publishing.publications["mavenKotlin"])
					sign(configurations.archives.get())
				}
				licenses {
					license {
						name = "GNU General Public License v3.0"
						url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
					}
				}
				developers {
					developer {
						id = "mikoe"
						name = "Miko Elbrecht"
						email = "miko@javart.zip"
					}
				}
				scm {
					connection = "scm:git:git://github.com/Bread-Experts-Group/bread_server_lib.git"
					developerConnection = "scm:git:ssh://git@github.com:Bread-Experts-Group/maven_micro_server.git"
					url = "https://javart.zip"
				}
			}
		}
	}
	repositories {
		maven {
			url = uri("https://maven.breadexperts.group/")
			credentials {
				username = localProperties["mavenUser"] as String
				password = localProperties["mavenPassword"] as String
			}
		}
	}
}
signing {
	useGpgCmd()
	sign(publishing.publications["mavenKotlin"])
}
tasks.javadoc {
	if (JavaVersion.current().isJava9Compatible) {
		(options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
	}
}

val generatedDir: String = layout.buildDirectory.get().asFile.resolve("generated").canonicalPath
val compileTime: String = ZonedDateTime.now().format(
	DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss.SSSSSS xxxxx")
)

val generateBuildInfo: TaskProvider<Task?> by tasks.registering {
	val outputDir = file(generatedDir)
	outputs.dir(outputDir)
	doLast {
		outputDir.mkdirs()
		val file = file("$generatedDir/BuildInfo.kt")
		file.writeText(
			"""package org.bread_experts_group

object BuildInfo {
	const val COMPILE_DATE = "$compileTime"
	const val VERSION = "$version"
}
"""
		)
	}
}

sourceSets["main"].kotlin.srcDir(generatedDir)
tasks.dokkaGeneratePublicationJavadoc { dependsOn(generateBuildInfo) }
tasks.kotlinSourcesJar {
	dependsOn(generateBuildInfo)
	dependsOn(tasks.compileJava)
}
tasks.compileKotlin {
	dependsOn(generateBuildInfo)
}