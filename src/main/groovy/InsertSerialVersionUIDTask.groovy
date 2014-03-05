import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.JavaExec

class InsertSerialVersionUIDTask extends JavaExec {
    def input = project.sourceSets.main.java.srcDirs

    def InsertSerialVersionUIDTask() {
        setMain('spoon.Launcher')
        setClasspath(project.rootProject.buildscript.configurations.classpath + project.sourceSets.main.runtimeClasspath)
        setArgs(['--compliance', '6', '--fragments',
                '-p', 'serialver.InsertSerialVersionUIDProcessor',
                '-i', input.toArray()[0],
                '--output', input.toArray()[0]])
        logging.captureStandardOutput LogLevel.INFO
    }

    def setSerialver(serialver) {
        systemProperties('serialver.insert.serialVersionUID': serialver)
    }
}
