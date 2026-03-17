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
}

val clean by tasks.registering(type = Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
