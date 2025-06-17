plugins {
    id("com.android.library")
    id("kotlin-kapt")
    kotlin("android")
}

android {
    namespace = "tv.purple.monolith"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = Config.jvmTarget
    }
}

tasks.configureEach {
    when (this.name) {
        "compileReleaseJavaWithJavac" -> this.doLast {
            delete(file("${layout.buildDirectory.get()}/intermediates/javac/release/compileReleaseJavaWithJavac/classes/tv/twitch"))
        }
        "bundleLibRuntimeToJarRelease" -> this.doFirst {
            copy {
                from("${rootProject.projectDir}/app/R.jar")
                into("${layout.buildDirectory.get()}/intermediates/compile_r_class_jar/release/generateReleaseRFile")
            }
        }
    }
}

dependencies {
    kapt(Dependencies.HiltCompiler)
    kapt(Dependencies.RoomCompiler)
    api(Dependencies.Bugsnag)

    api(project(":Twitch"))
}
