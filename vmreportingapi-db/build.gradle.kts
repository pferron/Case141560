import nu.studer.gradle.jooq.JooqEdition
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.MatcherRule
import org.jooq.meta.jaxb.MatcherTransformType.PASCAL
import org.jooq.meta.jaxb.Matchers
import org.jooq.meta.jaxb.MatchersTableType

buildscript {
    configurations {
        classpath {
            resolutionStrategy {
                eachDependency {
                    if (requested.group == "org.jooq") {
                        useVersion(Versions.jooq)
                    }
                }
            }
        }
    }
    dependencies {
        Dependencies.liquibase(this, "classpath")
        classpath("org.postgresql:postgresql:${Versions.postgresDriver}")
    }
}

plugins {
    id("com.target.vm.vmcommon.vm-java-library-plugin") version Versions.vmcommon
    id("nu.studer.jooq") version Versions.jooqPlugin
    id("org.liquibase.gradle") version Versions.liquibasePlugin
}

sourceSets { main { java { setSrcDirs(this.srcDirs + "src/jooq/java") } } }

dependencies {
    // public api
    api("org.jooq:jooq-codegen:${Versions.jooq}")
    api("jakarta.annotation:jakarta.annotation-api:${Versions.jakartaAnnotations}")
    api("org.jooq:jooq-postgres-extensions:${Versions.jooq}")

    // liquibase gradle plugin
    Dependencies.liquibase(this, "liquibaseRuntime")
    liquibaseRuntime("info.picocli:picocli:${Versions.picocli}")
    liquibaseRuntime("org.postgresql:postgresql:${Versions.postgresDriver}")

    // jooq gradle plugin
    jooqGenerator("org.postgresql:postgresql:${Versions.postgresDriver}")
    jooqGenerator("org.jooq:jooq-postgres-extensions:${Versions.jooq}")
    jooqGenerator("org.jooq:jooq-jackson-extensions:${Versions.jooq}")

    // tests
    Dependencies.liquibase(this, "testImplementation")
    testImplementation("org.postgresql:postgresql:${Versions.postgresDriver}")
}

val isVela = System.getenv("VELA_BUILD_COMMIT") != null
val postgresHostname = if (isVela) "postgres" else "localhost"

liquibase {
    jvmArgs = "-Duser.dir=$projectDir"
    runList = "main"
    activities.register("main") {
        this.arguments =
            mapOf(
                "changeLogFile" to "src/main/resources/db/changelog/build-changelog.yaml",
                "url" to "jdbc:postgresql://$postgresHostname:5434/postgres",
                "username" to "pguser",
                "password" to "pguser",
            )
    }
}

jooq {
    version.set(Versions.jooq)
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://$postgresHostname:5434/postgres"
                    user = "pguser"
                    password = "pguser"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        withForcedTypes(
                            ForcedType()
                                .withUserType(
                                    "com.target.vmreportingapi.db.EndpointDeviceAgentType")
                                .withEnumConverter(true)
                                .withIncludeExpression("endpoint_device_agents\\.type"),
                        )
                    }
                    generate.apply {
                        isRecords = true
                        isGeneratedAnnotation = true

                        isNullableAnnotation = true
                        nullableAnnotationType = "jakarta.annotation.Nullable"
                        isNonnullAnnotation = true
                        nonnullAnnotationType = "jakarta.annotation.Nonnull"
                    }
                    target.apply {
                        packageName = "com.target.vmreportingapi.db.generated"
                        directory = "$projectDir/src/jooq/java/"
                    }
                    strategy.apply {
                        name = "org.jooq.codegen.DefaultGeneratorStrategy"
                        matchers =
                            Matchers().apply {
                                tables =
                                    listOf(
                                        MatchersTableType().apply {
                                            tableClass =
                                                MatcherRule()
                                                    .withTransform(PASCAL)
                                                    .withExpression("$0_table")
                                        },
                                        MatchersTableType().apply {
                                            expression =
                                                "^(?:(?!.*(databasechangelog|databasechangeloglock|shedlock|v_)).*)$"
                                            recordImplements =
                                                "com.target.vmreportingapi.db.AuditableRecord"
                                        },
                                    )
                            }
                    }
                }
            }
        }
    }
}

// Remove coverage requirements for generated code
tasks.jacocoTestCoverageVerification {
    violationRules {
        isFailOnViolation = false
        rule { limit { minimum = null } }
    }
}
