plugins {
    application
    id("org.springframework.boot") version Versions.springBoot
    id("com.target.platform.connector.spring-boot-webmvc") version Versions.platformConnector
    id("com.target.vm.vmcommon.vm-java-plugin") version Versions.vmcommon
}

application { mainClass.set("com.target.vmreportingapi.Main") }

version = "0.0.1"

dependencies {
    implementation(project(":vmreportingapi-db"))
    implementation("com.target.vm.vmvendorapi:vmvendorapi-client:${Versions.vmvendorapi}")
    implementation("org.jetbrains:annotations:${Versions.jetbrainsAnnotations}")

    implementation("com.target.vm.vmcommon:vm-retrofit-clients:${Versions.vmcommon}")
    implementation("com.target.vm.vmcommon:vm-spring-common:${Versions.vmcommon}")

    implementation("com.squareup.retrofit2:converter-jackson:${Versions.retrofit}")
    implementation("org.springframework.boot:spring-boot-starter-actuator:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-aop:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-web:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-log4j2:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-security:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-validation:${Versions.springBoot}")
    implementation("io.micrometer:micrometer-registry-prometheus:${Versions.micrometer}")

    implementation("org.jooq:jooq:${Versions.jooq}")
    implementation("org.liquibase:liquibase-core:${Versions.liquibase}")
    implementation("org.postgresql:postgresql:${Versions.postgresDriver}")
    implementation("net.javacrumbs.shedlock:shedlock-spring:${Versions.shedlock}")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:${Versions.shedlock}")
    implementation("dnsjava:dnsjava:${Versions.dnsjava}")
    implementation("com.github.seancfoley:ipaddress:${Versions.ipaddress}")
    implementation("com.squareup.okhttp3:okhttp:${Versions.okhttp}")

    testImplementation("org.springframework.boot:spring-boot-starter-test:${Versions.springBoot}")
    testImplementation("org.spockframework:spock-spring:${Versions.spock}")
    testImplementation("com.squareup.okhttp3:mockwebserver:${Versions.okhttp}")
    testImplementation("com.squareup.retrofit2:retrofit-mock:${Versions.retrofit}")
}

tasks { distTar { archiveVersion.set("") } }
