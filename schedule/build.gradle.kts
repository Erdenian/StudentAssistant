import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
}

configure<JavaPluginConvention> {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

sourceSets {
  getByName("main").java.srcDirs("src/main/kotlin")
  getByName("test").java.srcDirs("src/test/kotlin")
}

dependencies {
  val kotlin_version: String by project
  val joda_time_version: String by project
  val guava_version: String by project

  implementation(kotlin("stdlib-jdk8", kotlin_version))

  implementation("joda-time:joda-time:$joda_time_version")
  implementation("com.google.guava:guava:$guava_version")
}
