// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.hilt.plugin) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.google.services) apply false
}

// Git hook 자동 설치 Task
tasks.register("installGitHook", Copy::class) {
    from("scripts") {
        include("pre-commit", "commit-msg", "post-commit")
    }
    into(rootProject.file(".git/hooks"))
    doLast {
        listOf("pre-commit", "commit-msg", "post-commit").forEach { hook ->
            file(".git/hooks/$hook").setExecutable(true, false)
        }
    }
}
