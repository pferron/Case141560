plugins {
    base
    // Use "./gradlew dependencyUpdates -Drevision=release" to check dependencies
    id("com.github.ben-manes.versions") version Versions.benManesVersions
}

subprojects {
    configurations {
        all {
            exclude(group = "ch.qos.logback")
            exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
            resolutionStrategy {
                cacheChangingModulesFor(0, TimeUnit.SECONDS)
                eachDependency {
                    if (this.requested.group == "org.apache.logging.log4j") {
                        this.useVersion(Versions.log4j)
                    }
                }
            }
        }
    }
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = Versions.gradle
        distributionUrl =
            "https://binrepo.target.com/artifactory/gradle-distributions-cache/gradle-${gradleVersion}-all.zip"
    }
}

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
    "Must be built with Java 17 or higher"
}
