dependencies {
//  compileOnly(libs.leycm.init)
    compileOnly(libs.annos.jetbrains)
    compileOnly(libs.annos.jspecify)

    compileOnly(project(":api"))
}

tasks.named("sourcesJar") {
    mustRunAfter(":api:jar")
}
