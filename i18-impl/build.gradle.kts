dependencies {
    compileOnly(libs.leycm.init)
    compileOnly(libs.annos.jetbrains)

    compileOnly(project(":api"))
}

tasks.named("sourcesJar") {
    mustRunAfter(":api:jar")
}
