dependencies {
    implementation(libs.leycm.init)
    implementation(libs.bundles.parser)
    implementation(project(":api"))
    compileOnly(libs.annos.jetbrains)

    // text libs for API compatibility
    compileOnly(libs.mcstructs.text)
    compileOnly(libs.bungee.chat)
    compileOnly(libs.adventure.gson)
    compileOnly(libs.adventure.plain)
    compileOnly(libs.adventure.legacy)
    compileOnly(libs.adventure.minimessage)
}

tasks.named("sourcesJar") {
    mustRunAfter(":api:jar")
}
