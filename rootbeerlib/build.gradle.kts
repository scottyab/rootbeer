plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
}

android {
    namespace = "com.scottyab.rootbeer"
    compileSdk =
        libs.versions.android.compile.sdk
            .get()
            .toInt()
    buildToolsVersion =
        libs.versions.android.build.tools
            .get()
    ndkVersion =
        libs.versions.android.ndk
            .get()

    defaultConfig {
        minSdk =
            libs.versions.android.min.sdk
                .get()
                .toInt()

        ndk {
            abiFilters.addAll(setOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a"))
        }
        externalNativeBuild {
            cmake {
                arguments.add("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
                // added to improve security of binary #180
                cFlags("-fPIC")
                cppFlags("-fPIC")
            }
        }
    }
    testOptions {
        targetSdk =
            libs.versions.android.target.sdk
                .get()
                .toInt()
    }
    lint {
        targetSdk =
            libs.versions.android.target.sdk
                .get()
                .toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
    java {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
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

// helper method to ensure we have a non null string for a property
fun getPropertyOrEmpty(propertyName: String): String = project.findProperty(propertyName)?.toString().orEmpty()

project.version = getPropertyOrEmpty("VERSION_NAME")
project.group = getPropertyOrEmpty("GROUP")

fun isReleaseBuild(): Boolean = !getPropertyOrEmpty("VERSION_NAME").contains("SNAPSHOT")

fun getReleaseRepositoryUrl(): String = getPropertyOrEmpty("RELEASE_REPOSITORY_URL")

fun getSnapshotRepositoryUrl(): String = getPropertyOrEmpty("SNAPSHOT_REPOSITORY_URL")

fun getRepositoryUsername(): String = getPropertyOrEmpty("NEXUS_USERNAME")

fun getRepositoryPassword(): String = getPropertyOrEmpty("NEXUS_PASSWORD")

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = getPropertyOrEmpty("GROUP")
            artifactId = getPropertyOrEmpty("POM_ARTIFACT_ID")
            version = getPropertyOrEmpty("VERSION_NAME")
            afterEvaluate {
                from(components["release"])
            }

            pom {
                name = getPropertyOrEmpty("POM_NAME")
                packaging = getPropertyOrEmpty("POM_PACKAGING")
                description = getPropertyOrEmpty("POM_DESCRIPTION")
                url = getPropertyOrEmpty("POM_URL")

                scm {
                    url = getPropertyOrEmpty("POM_SCM_URL")
                    connection = getPropertyOrEmpty("POM_SCM_CONNECTION")
                    developerConnection = getPropertyOrEmpty("POM_SCM_DEV_CONNECTION")
                }

                licenses {
                    license {
                        name = getPropertyOrEmpty("POM_LICENCE_NAME")
                        url = getPropertyOrEmpty("POM_LICENCE_URL")
                        distribution = getPropertyOrEmpty("POM_LICENCE_DIST")
                    }
                }

                developers {
                    developer {
                        id = getPropertyOrEmpty("POM_DEVELOPER_ID")
                        name = getPropertyOrEmpty("POM_DEVELOPER_NAME")
                        organizationUrl = getPropertyOrEmpty("POM_URL")
                    }
                }
            }
        }
    }
    repositories {
        maven(url = if (isReleaseBuild()) getReleaseRepositoryUrl() else getSnapshotRepositoryUrl()) {
            credentials {
                username = getRepositoryUsername()
                password = getRepositoryPassword()
            }
        }
    }
}

signing {
    setRequired({ isReleaseBuild() })
    sign(publishing.publications["release"])
}
