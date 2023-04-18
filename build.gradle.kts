buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.android.gradlePlugin)
    }
}

plugins {
    kotlin("multiplatform") version "1.8.0"
    id("com.android.library") version "7.3.1"
    id("maven-publish")
}

/* Library Specs */
val libAndroidNamespace: String by project
val libDeveloperOrg: String by project
val libMavenPublish: String by project
val libBaseName: String by project
val libBaseGroup: String by project
val libBaseVersion: String by project

group = libBaseGroup
version = libBaseVersion

repositories {
    mavenCentral()
    google()
}

kotlin {

    android {
        mavenPublication {
            artifactId = project.name
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.napier)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.bundles.testDependencies)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.lifecycle.viewmodel)
            }
        }

        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosX64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
        }

        val iosDeviceMain by creating {
            dependsOn(iosMain)
            iosArm64Main.dependsOn(this)
        }

        val iosSimulatorMain by creating {
            dependsOn(iosMain)
            iosSimulatorArm64Main.dependsOn(this)
            iosX64Main.dependsOn(this)
        }

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

// Configuration for Android package publishing to GitHub Package Registry
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/$libDeveloperOrg/$libMavenPublish")

            val ghUsername: String? = System.getenv("GH_USERNAME") ?: properties["GH_USERNAME"]?.toString()
            val ghToken: String? = System.getenv("GH_TOKEN") ?: properties["GH_TOKEN"]?.toString()

            credentials {
                username = ghUsername
                password = ghToken
            }
        }
    }
}

android {
    namespace = libAndroidNamespace
    compileSdk = (findProperty("android.compileSdk") as String).toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = false
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}
