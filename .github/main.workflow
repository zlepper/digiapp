workflow "Build" {
  on = "push"
  resolves = ["build project"]
}

action "build project" {
  uses = "vgaidarji/android-github-actions/build@v1.0.0"
  args = "./gradlew assembleDebug"
}
