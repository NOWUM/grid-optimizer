plugins {
    application
    kotlin("jvm") version "1.5.20"
}

group = "de.fhac.ewi"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenLocal()
    jcenter()
    maven(url = "https://jitpack.io")
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.20")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1-native-mt")

    implementation("io.ktor:ktor-server-netty:1.6.1")
    implementation("io.ktor:ktor-server-core:1.6.1")
    implementation("io.ktor:ktor-server-host-common:1.6.1")
    implementation("io.ktor:ktor-server-sessions:1.6.1")
    implementation("io.ktor:ktor-gson:1.6.1")
    testImplementation("io.ktor:ktor-server-tests:1.6.1")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("io.insert-koin:koin-core:3.1.2")
    implementation("io.insert-koin:koin-ktor:3.1.2")
    testImplementation("io.insert-koin:koin-test:3.1.2")


    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.2")

    implementation("com.github.EvanRupert:ExcelKt:v0.1.1")

}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

val fatJar = task("fatJar", type = Jar::class) {
    // Only copy files once. 2nd attempt will be ignored without warning (Gradle 6.x) or exception (Gradle 7.x)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Implementation-Title"] = "Grid Optimizer - Fat"
        attributes["Implementation-Version"] = project.version
        attributes["Main-Class"] = "io.ktor.server.netty.EngineMain"
    }

    // Copy all needed dependencies for kotlin
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/**")
    }

    // Copy frontend
    from(file("src-frontend")) {
        include("build/**")
    }

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
                    // for hot reloading (enabled)
                    "KTOR_MODULE" to "build",
                    "KTOR_AUTORELOAD" to "true",
                    "KTOR_DEVMODE" to "true",
                    // information about this build for /version route
                    "PROJECT_VERSION" to version,
                    "BUILD_TIMESTAMP" to buildTimestamp
                )
            }
            "production" -> {
                expand(
                    "KTOR_ENV" to "production",
                    "KTOR_PORT" to "80",
                    // for hot reloading (disabled)
                    "KTOR_MODULE" to "",
                    "KTOR_AUTORELOAD" to "false",
                    "KTOR_DEVMODE" to "false",
                    // information about this build for /version route
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
        dependsOn(yarnBuild)
    }

    compileKotlin {
        kotlinOptions {
            freeCompilerArgs =
                freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn" // Opt-in option for Koin annotation of KoinComponent.
        }
    }
}
