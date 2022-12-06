import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("io.realm.kotlin")
}

group = "com.globalpay.vpos"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    //maven ("https://jitpack.io" )
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                // ktor dependencies
                val ktor_version = "2.1.3"
                implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
                implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
                implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
                implementation("io.ktor:ktor-serialization-gson:$ktor_version")
                implementation("io.ktor:ktor-network:$ktor_version")
                // persistence realmdb
                implementation("io.realm.kotlin:library-base:1.4.0")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "vPOS"
            packageVersion = "1.0.0"
        }
    }
}
