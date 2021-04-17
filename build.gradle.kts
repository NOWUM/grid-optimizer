val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.21"
}

group = "de.fhac.ewi"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-gson:$ktor_version")

    implementation("org.koin:koin-core:$koin_version")
    implementation("org.koin:koin-ktor:$koin_version")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.koin:koin-test:$koin_version")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
val fatJar = task("fatJar", type = Jar::class) {
    manifest {
        attributes["Implementation-Title"] = "Grid Optimizer - Fat"
        attributes["Implementation-Version"] = project.version
        attributes["Main-Class"] = "io.ktor.server.netty.EngineMain"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    from(sourceSets["main"].resources)
    with(tasks.jar.get() as CopySpec)
    archiveFileName.set("server.jar")
}

val yarnBuild = task<Exec>("yarnBuild") {
    workingDir = file("src-frontend")
    if (System.getProperty("os.name").toLowerCase().contains("windows"))
        commandLine("cmd.exe", "/C", "yarn.cmd run build")
    else // assume *nix.
        commandLine("yarn", "run", "build") // NB untested
}

val copyDistFolder = tasks.register<Copy>("copyDistFolder") {
    delete("resources/dist")
    from(file("src-frontend/dist"))
    into(file("resources/dist"))
}

var env = "production"
val buildTimestamp = System.currentTimeMillis()

tasks.processResources {
    outputs.upToDateWhen { false }
    filesMatching("*.conf") {
        // Replace environment specific variables
        when (env) {
            "development" -> {
                expand(
                    "KTOR_ENV" to "dev",
                    "KTOR_PORT" to "8080",
                    // For hot reloading
                    "KTOR_MODULE" to "build",
                    "KTOR_AUTORELOAD" to "true",
                    "KTOR_DEVMODE" to "true",
                    // for /version route
                    "PROJECT_VERSION" to version,
                    "BUILD_TIMESTAMP" to buildTimestamp
                )
            }
            "production" -> {
                expand(
                    "KTOR_ENV" to "production",
                    "KTOR_PORT" to "80",
                    // For hot reloading
                    "KTOR_MODULE" to "",
                    "KTOR_AUTORELOAD" to "false",
                    "KTOR_DEVMODE" to "false",
                    // for /version route
                    "PROJECT_VERSION" to version,
                    "BUILD_TIMESTAMP" to buildTimestamp
                )
            }
        }
    }
}

val setDev = tasks.register("setDev") {
    env = "development"
}

tasks {
    "run" {
        dependsOn(setDev)
    }
    "build" {
        dependsOn(fatJar)
        doLast {
            copy {
                delete("bundle")
                from(fatJar)
                into(file("bundle"))
            }
        }
    }
    "fatJar" {
        dependsOn(copyDistFolder)
    }
    "copyDistFolder" {
        // TODO dependsOn(yarnBuild)
    }

    compileKotlin {
        kotlinOptions {
            freeCompilerArgs =
                freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn" // Opt-in option for Koin annotation of KoinComponent.
        }
    }
}
