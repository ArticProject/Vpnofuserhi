import java.security.MessageDigest
import java.io.File
import java.net.URI

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.vellorvpn.vxqtz"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "1.2-subscriptions"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    // The app uses no geoip/geosite rules. Avoid bundling unused country databases.
    androidResources { ignoreAssetsPattern = "geoip.dat:geoip-only-cn-private.dat:geosite.dat" }
    packaging { jniLibs.useLegacyPackaging = true }
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86_64")
            isUniversalApk = false
        }
    }

    buildFeatures {
        compose = true
    }
}

// Official AndroidLibXrayLite release, pinned and verified before use.
val xrayAar = layout.buildDirectory.file("generated/xray/libv2ray-26.9.9.aar")
val prepareXray by tasks.registering {
    outputs.file(xrayAar)
    doLast {
        val target = xrayAar.get().asFile
        target.parentFile.mkdirs()
        val expected = "9ecf4c921568d8f4cb8550d3bafe08ff6f1d1984f45a6ad183dcdf52ee9302de"
        fun digest(file: File) = MessageDigest.getInstance("SHA-256").digest(file.readBytes())
            .joinToString("") { "%02x".format(it) }
        if (target.exists() && digest(target) == expected) return@doLast
        val temporary = File(target.parentFile, "download.tmp")
        try {
            URI("https://github.com/2dust/AndroidLibXrayLite/releases/download/v26.9.9/libv2ray.aar")
                .toURL().openConnection().apply { connectTimeout = 30000; readTimeout = 120000 }
                .getInputStream().use { input -> temporary.outputStream().use { input.copyTo(it) } }
            check(digest(temporary) == expected) {
                "Xray dependency checksum mismatch"
            }
            check(temporary.renameTo(target)) { "Could not install verified Xray dependency" }
        } finally {
            temporary.delete()
        }
    }
}

dependencies {
    implementation(files(xrayAar).builtBy(prepareXray))
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.16.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.coil.compose)

    debugImplementation(libs.androidx.ui.tooling)
}
