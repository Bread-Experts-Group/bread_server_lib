import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

plugins {
	kotlin("jvm") version "2.3.0"
	`maven-publish`
	`java-library`
	signing
}

group = "org.bread_experts_group"
version = (findProperty("libVersion") as String)
// Bread Experts Group Versioning System, revision 1
//                          Pertains to the ...
// Dx ... Design x       //  entire architecture of the project, like usage of I/O in features/native
// Fx ... Feature Set x  //  current feature set exposed by the project
// Nx ... Native Set x   //  current native set supported by this current feature set
//                       //   (native sets are any additions to the native OS/hardware support of the project)
// Px ... Patch x        //  current code revision, like a fix or logic change
// This system does not prescribe "safe" versions to update to, like that of major/minor/patch in semantic versioning
// Check before updating or do not update at all

repositories {
	mavenCentral()
}
dependencies {
	testImplementation(kotlin("test"))
	implementation(kotlin("reflect"))
}
tasks.test {
	useJUnitPlatform()
	maxHeapSize = "20G"
}
kotlin {
	jvmToolchain(25)
	compilerOptions {
		freeCompilerArgs.add("-Xcontext-parameters")
		freeCompilerArgs.add("-Xannotations-in-metadata")
	}
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
			pom {
				name = "Bread Server Library"
				description = "Distribution of software for Bread Experts Group operated servers."
				url = "https://breadexperts.group"
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
						email = "miko@breadexperts.group"
					}
				}
				scm {
					connection = "scm:git:git://github.com/Bread-Experts-Group/bread_server_lib.git"
					developerConnection = "scm:git:ssh://git@github.com:Bread-Experts-Group/maven_micro_server.git"
					url = "https://breadexperts.group"
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

val projectVersion = providers.provider { project.version.toString() }
val generateBuildInfo: TaskProvider<Task> by tasks.registering {
	val generatedDir = project.layout.buildDirectory.get().asFile.resolve("generated")
	outputs.dir(generatedDir)
	doFirst {
		generatedDir.mkdirs()
		val file = File("$generatedDir/BuildInfo.kt")
		val compileTime: String = ZonedDateTime.now().format(
			DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss.SSSSSS xxxxx")
		)
		file.writeText(
			"""package org.bread_experts_group

internal object BuildInfo {
	const val COMPILE_DATE = "$compileTime"
	const val VERSION = "${project.findProperty("libVersion") as String}"
}
"""
		)
	}
}

sourceSets {
	main {
		kotlin.srcDirs(
			"src/main/kotlin",
			project.layout.buildDirectory.get().asFile.resolve("generated").canonicalPath
		)
	}
}

tasks.publishToMavenLocal { dependsOn(tasks.jar) }
tasks.kotlinSourcesJar { dependsOn(tasks.compileJava, generateBuildInfo) }
tasks.compileKotlin { dependsOn(generateBuildInfo) }
tasks.jar { dependsOn(generateBuildInfo) }