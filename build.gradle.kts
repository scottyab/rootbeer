import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.BasePlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
}

allprojects {
    version = findProperty("VERSION_NAME").toString()
    group = findProperty("GROUP").toString()
}

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    plugins.withType<BasePlugin>().configureEach {
        extensions.configure<BaseExtension> {
            compileSdkVersion(libs.versions.android.compile.sdk.get().toInt())
            buildToolsVersion(libs.versions.android.build.tools.get())
            ndkVersion = libs.versions.android.ndk.get()

            defaultConfig {
                minSdk = libs.versions.android.min.sdk.get().toInt()
                targetSdk = libs.versions.android.target.sdk.get().toInt()
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }

        extensions.findByType<LibraryExtension>()?.apply {
            lint {
                targetSdk = libs.versions.android.target.sdk.get().toInt()
            }
        }
    }

    plugins.withType<MavenPublishPlugin>().configureEach {
        extensions.configure<PublishingExtension> {
            repositories {
                val repositoryUrl = if (isReleaseBuild) {
                    requireNotNull(property("RELEASE_REPOSITORY_URL")?.toString())
                } else {
                    requireNotNull(property("SNAPSHOT_REPOSITORY_URL")?.toString())
                }

                maven {
                    url = uri(repositoryUrl)

                    credentials {
                        username = findStringPropertyOrDefault("NEXUS_USERNAME")
                        password = findStringPropertyOrDefault("NEXUS_PASSWORD")
                    }
                }
            }
        }
    }

    plugins.withType<SigningPlugin>().configureEach {
        extensions.configure<SigningExtension> {
            isRequired = isReleaseBuild
        }
    }
}

val clean by tasks.registering(type = Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

private val Project.isReleaseBuild get() = !version.toString().endsWith("SNAPSHOT")

private fun Project.findStringPropertyOrDefault(propertyName: String, default: String? = "") =
    findProperty(propertyName)?.toString() ?: default
