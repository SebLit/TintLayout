plugins {
    id("com.android.library")
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.seblit.android.tintlayout"
            artifactId = "tintlayout"
            version = "1.0.0"
            artifact(layout.buildDirectory.file("outputs/aar/app-release.aar"))
        }
    }

    repositories {
        maven {
            name = "Github_Packages"
            url = uri("https://maven.pkg.github.com/SebLit/TintLayout")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

android {
    namespace = "com.seblit.android.widget.tintlayout"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
}