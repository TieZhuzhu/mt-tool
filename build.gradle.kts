plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.16.1"
    id("com.github.johnrengelman.shadow") version "8.1.1" // 添加shadow插件
}

group = "com.augustlee"
version = "1.0.2-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.alibaba:fastjson:2.0.42")
}

sourceSets {
    main {
        java.srcDirs("src/main/java", "src/main/kotlin")
    }
}

// Configure Gradle IntelliJ Plugin
intellij {
    version.set("2023.1.5")
    type.set("IC")
    plugins.set(listOf("java"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("252.*")
    }

    // 配置shadowJar任务
    shadowJar {
        archiveBaseName.set("mt-plugin") // JAR包基础名称
        archiveClassifier.set("") // 不使用分类器，避免生成带额外后缀的JAR
        archiveVersion.set(version.toString())

        // 合并META-INF/plugin.xml文件
        mergeServiceFiles()

        // 排除不需要的文件
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

        // 指定主类（如果有）
        // manifest {
        //     attributes("Main-Class" to "com.kation.mt.Main")
        // }

        // 将依赖打包进JAR
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }

    // 替换默认的jar任务为shadowJar
    jar {
        enabled = false
    }

    // 确保build插件任务使用shadowJar生成的JAR
    buildPlugin {
        dependsOn(shadowJar)
        from(shadowJar.get().archiveFile)
    }
}