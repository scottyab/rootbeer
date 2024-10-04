plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.scottyab.rootbeer.sample"

    defaultConfig {
        applicationId = "com.scottyab.rootbeer.sample"
        versionName = version.toString()
        versionCode = findProperty("VERSION_CODE").toString().toInt()
        vectorDrawables.useSupportLibrary = true

        base.archivesName = "RootBeerSample-$versionName-[$versionCode]"
    }

    buildFeatures {
        viewBinding = true
    }

    // check if the keystore details are defined in gradle.properties (this is so the key is not in github)
    if (findProperty("ROOTBEER_SAMPLE_STORE") != null) {
        signingConfigs {
            // from ~/user/.gradle/gradle.properties
            create("release") {
                storeFile = file(findProperty("ROOTBEER_SAMPLE_STORE").toString())
                keyAlias = findProperty("ROOTBEER_SAMPLE_KEY").toString()
                storePassword = findProperty("ROOTBEER_SAMPLE_STORE_PASS").toString()
                keyPassword = findProperty("ROOTBEER_SAMPLE_KEY_PASS").toString()
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }

        release {
            if (rootProject.hasProperty("ROOTBEER_SAMPLE_STORE")) {
                signingConfig = signingConfigs["release"]
            }

            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":rootbeerlib"))
    // used when testing the snapshot lib
    // implementation("com.scottyab:rootbeer-lib:0.1.1-SNAPSHOT")

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.android)

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.appcompat)
    implementation(libs.android.google.material)

    implementation(libs.nineoldandroids)
    implementation(libs.beerprogressview)

    implementation(libs.timber)
}
