import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

class InsertSerialVersionUIDTask extends JavaExec {
    def input = project.sourceSets.main.java.srcDirs

    def InsertSerialVersionUIDTask() {
        setMain('spoon.Launcher')
        setClasspath(project.rootProject.buildscript.configurations.classpath +
                project.sourceSets.main.runtimeClasspath)
        logging.captureStandardOutput LogLevel.INFO
    }

    @TaskAction
    public void exec() {
        input.eachWithIndex { dir, i ->
            setArgs(['--compliance', '6', '--fragments',
                    '-p', 'serialver.InsertSerialVersionUIDProcessor',
                    '-i', dir,
                    '--output', temporaryDirForIndex(i)])
            super.exec()
        }
        input.eachWithIndex { dir, i ->
            ant.copy( todir: dir ) {
                fileset( dir: temporaryDirForIndex(i) )
            }
        }
    }

    def temporaryDirForIndex(i) {
        [temporaryDir, "serialver", i].join(File.separator)
    }

    def setSerialver(serialver) {
        systemProperties('serialver.insert.serialVersionUID': serialver)
    }
}
