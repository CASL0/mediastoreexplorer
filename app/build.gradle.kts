import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.aboutlibraries.android)
    jacoco
}

val versionMajor = 1 // x-release-please-major
val versionMinor = 3 // x-release-please-minor
val versionPatch = 1 // x-release-please-patch

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties =
    Properties().apply {
        if (keystorePropertiesFile.exists()) {
            load(FileInputStream(keystorePropertiesFile))
        }
    }

android {
    namespace = "io.github.casl0.mediastoreexplorer"
    compileSdk { version = release(36) { minorApiLevel = 1 } }

    defaultConfig {
        applicationId = "io.github.casl0.mediastoreexplorer"
        minSdk = 24
        targetSdk = 36
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName = "$versionMajor.$versionMinor.$versionPatch"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            when {
                keystorePropertiesFile.exists() -> {
                    storeFile = file(keystoreProperties["storeFile"] as String)
                    storePassword = keystoreProperties["storePassword"] as String
                    keyAlias = keystoreProperties["keyAlias"] as String
                    keyPassword = keystoreProperties["keyPassword"] as String
                }
                System.getenv("KEYSTORE_PATH") != null -> {
                    storeFile = file(System.getenv("KEYSTORE_PATH"))
                    storePassword = System.getenv("KEYSTORE_PASSWORD")
                    keyAlias = System.getenv("KEY_ALIAS")
                    keyPassword = System.getenv("KEY_PASSWORD")
                }
            // どちらも無い場合は storeFile 未設定のままにし、release は
            // debug 署名にフォールバックして検証だけ可能にする
            }
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // keystore.properties か環境変数のどちらかが存在する場合のみ release
            // 署名を当てる。両方無いローカル検証では debug 署名にフォールバックする
            signingConfig =
                if (signingConfigs.getByName("release").storeFile != null) {
                    signingConfigs.getByName("release")
                } else {
                    signingConfigs.getByName("debug")
                }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    androidResources { generateLocaleConfig = true }
}

aboutLibraries { export { prettyPrint = true } }

kotlin { compilerOptions { jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11 } }

val jacocoExcludes =
    listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        // Hilt / Dagger 生成クラス
        "**/*_MembersInjector.class",
        "**/Dagger*Component*.class",
        "**/*_Factory.class",
        "**/*HiltComponents*.class",
        "**/*Hilt_*.class",
        "**/*_HiltModules*.class",
        "**/hilt_aggregated_deps/**",
        // Compose 生成クラス
        "**/*ComposableSingletons*.*",
    )

// 単体テスト + インストルメンテッドテスト の合算カバレッジレポート
tasks.register<JacocoReport>("jacocoDebugCoverageReport") {
    dependsOn("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(
        fileTree(
            layout.buildDirectory.dir(
                "intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes"
            )
        ) {
            exclude(jacocoExcludes)
        }
    )
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include(
                "outputs/unit_test_code_coverage/debugUnitTest/*.exec",
                "outputs/code_coverage/debugAndroidTest/connected/**/*.ec",
            )
        }
    )
}

// 単体テストのみのカバレッジレポート（デバイス不要）
tasks.register<JacocoReport>("jacocoUnitTestCoverageReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(
        fileTree(
            layout.buildDirectory.dir(
                "intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes"
            )
        ) {
            exclude(jacocoExcludes)
        }
    )
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include("outputs/unit_test_code_coverage/debugUnitTest/*.exec")
        }
    )
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.coil.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.accompanist.permissions)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
