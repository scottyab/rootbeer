plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = findStringPropertyOrDefault("POM_ARTIFACT_ID")

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name = findStringPropertyOrDefault("POM_NAME")
                packaging = findStringPropertyOrDefault("POM_PACKAGING")
                description = findStringPropertyOrDefault("POM_DESCRIPTION")
                url = findStringPropertyOrDefault("POM_URL")

                scm {
                    url = findStringPropertyOrDefault("POM_SCM_URL")
                    connection = findStringPropertyOrDefault("POM_SCM_CONNECTION")
                    developerConnection = findStringPropertyOrDefault("POM_SCM_DEV_CONNECTION")
                }

                licenses {
                    license {
                        name = findStringPropertyOrDefault("POM_LICENCE_NAME")
                        url = findStringPropertyOrDefault("POM_LICENCE_URL")
                        distribution = findStringPropertyOrDefault("POM_LICENCE_DIST")
                    }
                }

                developers {
                    developer {
                        id = findStringPropertyOrDefault("POM_DEVELOPER_ID")
                        name = findStringPropertyOrDefault("POM_DEVELOPER_NAME")
                        organizationUrl = findStringPropertyOrDefault("POM_URL")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["release"])
}

private fun Project.findStringPropertyOrDefault(propertyName: String, default: String? = "") =
    findProperty(propertyName)?.toString() ?: default
