plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
}

android {
    namespace = "com.scottyab.rootbeer"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()
    buildToolsVersion = libs.versions.android.build.tools.get()
    ndkVersion = libs.versions.android.ndk.get()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()

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
        targetSdk = libs.versions.android.target.sdk.get().toInt()
    }
    lint {
        targetSdk = libs.versions.android.target.sdk.get().toInt()
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
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
}

fun getPropertyOrDefault(propertyName: String, default: String = ""): String {
    return project.findProperty(propertyName)?.toString() ?: default
}

project.version = getPropertyOrDefault(propertyName = "VERSION_NAME")
project.group = getPropertyOrDefault(propertyName = "GROUP")

fun isReleaseBuild(): Boolean {
    return !getPropertyOrDefault(
        propertyName = "VERSION_NAME",
    ).contains("SNAPSHOT")
}

fun getReleaseRepositoryUrl(): String = getPropertyOrDefault(
    propertyName = "RELEASE_REPOSITORY_URL",
    default = "https://oss.sonatype.org/service/local/staging/deploy/maven2/",
)

fun getSnapshotRepositoryUrl(): String = getPropertyOrDefault(
    propertyName = "SNAPSHOT_REPOSITORY_URL",
    default = "https://oss.sonatype.org/content/repositories/snapshots/",
)

fun getRepositoryUsername(): String = getPropertyOrDefault(
    propertyName = "NEXUS_USERNAME",
)

fun getRepositoryPassword(): String = getPropertyOrDefault(
    propertyName = "NEXUS_PASSWORD",
)

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = getPropertyOrDefault("GROUP")
            artifactId = getPropertyOrDefault("POM_ARTIFACT_ID")
            version = getPropertyOrDefault("VERSION_NAME")
            afterEvaluate {
                from(components["release"])
            }

            pom {
                name = getPropertyOrDefault("POM_NAME")
                packaging = getPropertyOrDefault("POM_PACKAGING")
                description = getPropertyOrDefault("POM_DESCRIPTION")
                url = getPropertyOrDefault("POM_URL")

                scm {
                    url = getPropertyOrDefault("POM_SCM_URL")
                    connection = getPropertyOrDefault("POM_SCM_CONNECTION")
                    developerConnection = getPropertyOrDefault("POM_SCM_DEV_CONNECTION")
                }

                licenses {
                    license {
                        name = getPropertyOrDefault("POM_LICENCE_NAME")
                        url = getPropertyOrDefault("POM_LICENCE_URL")
                        distribution = getPropertyOrDefault("POM_LICENCE_DIST")
                    }
                }

                developers {
                    developer {
                        id = getPropertyOrDefault("POM_DEVELOPER_ID")
                        name = getPropertyOrDefault("POM_DEVELOPER_NAME")
                    }
                }
            }
        }
    }
    repositories {
        maven(url = getReleaseRepositoryUrl()) {
            credentials {
                username = getRepositoryUsername()
                password = getRepositoryPassword()
            }
        }
        maven(url = getSnapshotRepositoryUrl()) {
            name = "snapshot"
            credentials {
                username = getRepositoryUsername()
                password = getRepositoryPassword()
            }
        }
    }
}

signing {
    setRequired({
        isReleaseBuild()
    })
    sign(publishing.publications["release"])
}