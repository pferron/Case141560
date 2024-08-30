import org.gradle.kotlin.dsl.DependencyHandlerScope

object Dependencies {
    fun liquibase(scope: DependencyHandlerScope, configurationName: String) {
        scope.add(configurationName, "org.liquibase:liquibase-core:${Versions.liquibase}")
        scope.add(configurationName, "org.yaml:snakeyaml:${Versions.snakeyaml}")
    }
}