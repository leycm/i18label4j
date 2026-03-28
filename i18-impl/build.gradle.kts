dependencies {
    implementation(libs.leycm.init)
    implementation(libs.bundles.parser)
    implementation(project(":api"))
    compileOnly(libs.annos.jetbrains)

    compileOnly(libs.mcstructs.text)
    compileOnly(libs.adventure.gson)
    compileOnly(libs.adventure.plain)
    compileOnly(libs.adventure.legacy)
    compileOnly(libs.adventure.minimessage)
}

tasks.named("sourcesJar") {
    mustRunAfter(":api:jar")
}
