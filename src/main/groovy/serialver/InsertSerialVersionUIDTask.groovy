package serialver

import com.darylteo.gradle.javassist.tasks.TransformationTask
import org.gradle.api.tasks.TaskAction

public class InsertSerialVersionUIDTask extends TransformationTask {

    def serialver
    def overwrite

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

        if (overwrite != null) {
            setTransformation(new SerialVersionUIDTransformer(serialVerAsLong, overwrite))
        } else {
            setTransformation(new SerialVersionUIDTransformer(serialVerAsLong))
        }

        // in place transformation
        from(project.sourceSets.main.output[0])
        into(project.sourceSets.main.output[0])

        super.exec()
    }

}
