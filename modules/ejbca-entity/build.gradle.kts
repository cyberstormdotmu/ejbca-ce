import java.util.Properties

val props: Properties = Properties().apply {
    val propertiesFilePath = "${rootProject.projectDir}/conf/database.properties"
    if (file(propertiesFilePath).exists()) {
        load(file(propertiesFilePath).inputStream())
    } else {
        load(file(propertiesFilePath + ".sample").inputStream())
    }
}

plugins {
    java
}

dependencies {
    compileOnly(project(":modules:ejbca-common"))
    compileOnly(project(":modules:cesecore-common"))
    compileOnly(project(":modules:cesecore-entity"))
    compileOnly(libs.jakartaee.api)
    compileOnly(libs.log4j.v12.api)
    compileOnly(libs.commons.lang)
    compileOnly(libs.x509.common.util)
}

sourceSets {
    val main by getting {
        java {
            setSrcDirs(listOf("src"))
        }
    }
}

tasks.jar {
    from(sourceSets["main"].output)
    from("resources") {
        include("orm-ejbca-mysql.xml")
        into("META-INF")
    }
    from("resources") {
        include("persistence-ds-template.xml")
        rename("persistence-ds-template.xml", "persistence.xml")
        into("META-INF")
        filter { line: String ->
            line.replace("\${datasource.jndi-name-prefix}", "java:/")
                .replace("\${datasource.jndi-name}", props.getProperty("datasource.jndi-name", "EjbcaDS"))
                .replace("\${database.name}", props.getProperty("database.name", "mysql"))
        }
    }
}