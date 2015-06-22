package serialver

import com.darylteo.gradle.javassist.tasks.TransformationTask
import org.gradle.api.tasks.TaskAction

public class InsertSerialVersionUIDTask extends TransformationTask {

    def serialver

    InsertSerialVersionUIDTask() {
        dependsOn(project.classes)
        project.jar.mustRunAfter(this)
    }

    @TaskAction
    public void exec() {
        classpath += project.configurations.compile

        def serialVerAsLong = Long.parseLong(serialver.replaceAll('L', '').replaceAll('l', ''))
        setTransformation(new SerialVersionUIDTransformer(serialVerAsLong))

        // in place transformation
        from(project.sourceSets.main.output[0])
        into(project.sourceSets.main.output[0])

        super.exec()
    }

}
