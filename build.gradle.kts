// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.hilt.plugin) apply false
    alias(libs.plugins.ktlint) apply false
}

// Git hook 자동 설치 Task
tasks.register("installGitHook", Copy::class) {
    from("scripts") {
        include("pre-commit", "commit-msg")
    }
    into(file(".git/hooks"))
    doLast {
        file(".git/hooks/pre-commit").setExecutable(true, false)
        file(".git/hooks/commit-msg").setExecutable(true, false)
    }
}
