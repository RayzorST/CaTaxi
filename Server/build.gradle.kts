
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.project"
version = "0.0.1"

application {
    mainClass = "com.project.ApplicationKt"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.cio)
    implementation(libs.logback.classic)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation("org.jetbrains.exposed:exposed-dao:0.60.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.60.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.h2database:h2:2.2.224")
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
