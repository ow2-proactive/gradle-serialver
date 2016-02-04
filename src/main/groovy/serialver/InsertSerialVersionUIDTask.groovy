package serialver

import com.darylteo.gradle.javassist.tasks.TransformationTask
import org.gradle.api.tasks.TaskAction

public class InsertSerialVersionUIDTask extends TransformationTask {

    def serialver
    def overwrite = true
    def forceUIDOnThrowable = false

    InsertSerialVersionUIDTask() {
        dependsOn(project.classes)
        project.jar.mustRunAfter(this)
    }

    @TaskAction
    public void exec() {
        classpath += project.configurations.compile
        def serialVerAsLong
        if (serialver instanceof String) {
            serialVerAsLong = Long.parseLong(serialver.replaceAll('L', '').replaceAll('l', ''))
        } else {
            serialVerAsLong = serialver
        }

        setTransformation(new SerialVersionUIDTransformer(serialVerAsLong, overwrite, forceUIDOnThrowable))

        // in place transformation
        from(project.sourceSets.main.output[0])
        into(project.sourceSets.main.output[0])

        super.exec()
    }

}
