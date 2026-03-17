plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.vanniktech.maven.publish)
}

android {
    namespace = "com.scottyab.rootbeer"

    defaultConfig {
        ndk {
            abiFilters.addAll(setOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a"))
        }

        @Suppress("UnstableApiUsage")
        externalNativeBuild {
            cmake {
                arguments.add("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
                // added to improve security of binary #180
                cFlags("-fPIC")
                cppFlags("-fPIC")
            }
        }
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        targetSdk = libs.versions.android.target.sdk.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
}

mavenPublishing {
    coordinates(
        artifactId = "rootbeer-lib"
    )

    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
