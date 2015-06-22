package serialver

import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.testng.AssertJUnit.assertEquals

public class InsertSerialVersionUIDTaskTest {

    @Test
    public void shouldAddSerialVersionUIDField() throws Exception {
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: 'java')

        project.task("serialver", type: InsertSerialVersionUIDTask.class) {
            serialver = '42L'
        }

        createSerializableJavaSource(project)

        project.compileJava.execute()
        project.serialver.execute()

        assertEquals(42L, getCompiledClass(project).getField("serialVersionUID").getConstantValue())
    }

    @Test
    public void shouldUpdateSerialVersionUIDField() throws Exception {
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: 'java')

        project.task("serialver", type: InsertSerialVersionUIDTask.class) {
            serialver = '42L'
        }

        createSerializableJavaSourceWithSerialVersionUID(project)

        project.compileJava.execute()
        project.serialver.execute()

        assertEquals(42L, getCompiledClass(project).getField("serialVersionUID").getConstantValue())
    }

    private void createSerializableJavaSource(Project project) {
        FileUtils.forceMkdir(project.sourceSets.main.java.srcDirs[0])

        new File(project.sourceSets.main.java.srcDirs[0], 'SerializableClass.java') <<
                'import java.io.Serializable;\n' +
                'public class SerializableClass implements Serializable {\n' +
                '}'
    }

    private void createSerializableJavaSourceWithSerialVersionUID(Project project) {
        FileUtils.forceMkdir(project.sourceSets.main.java.srcDirs[0])

        new File(project.sourceSets.main.java.srcDirs[0], 'SerializableClass.java') <<
                'import java.io.Serializable;\n' +
                'public class SerializableClass implements Serializable {\n' +
                'private static final long serialVersionUID = 7L; \n' +
                '}'
    }

    private CtClass getCompiledClass(Project project) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new FileInputStream(new File(project.sourceSets.main.output[0], 'SerializableClass.class')));
        ctClass
    }
}