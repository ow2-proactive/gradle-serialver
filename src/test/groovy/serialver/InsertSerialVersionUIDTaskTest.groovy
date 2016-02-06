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
        shouldAddSerialVersionUIDField(false, 42L)
    }

    @Test
    public void shouldAddSerialVersionUIDFieldWithForceException() throws Exception {
        shouldAddSerialVersionUIDField(true, 1L)
    }

    public void shouldAddSerialVersionUIDField(boolean forceException, long expectedUIDException) throws Exception {
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: 'java')

        project.task("serialver", type: InsertSerialVersionUIDTask.class) {
            serialver = '42L'
            overwrite = true
            forceUIDOnException = forceException
        }

        createSerializableJavaSource(project)
        createExceptionJavaSource(project)

        project.compileJava.execute()
        project.serialver.execute()

        assertEquals(42L, getCompiledSerialzableClass(project).getField("serialVersionUID").getConstantValue())
        assertEquals(expectedUIDException, getCompiledExceptionClass(project).getField("serialVersionUID").getConstantValue())
    }

    @Test
    public void shouldUpdateSerialVersionUIDFieldWithoutOverwriteAndForceException() throws Exception {
        shouldUpdateSerialVersionUIDField(false, true, 7L, 1L);
    }

    @Test
    public void shouldUpdateSerialVersionUIDFieldWithOverwriteAndForceException() throws Exception {
        shouldUpdateSerialVersionUIDField(true, true, 42L, 1L);
    }

    @Test
    public void shouldUpdateSerialVersionUIDFieldWithOverwrite() throws Exception {
        shouldUpdateSerialVersionUIDField(true, false, 42L, 42L);
    }

    @Test
    public void shouldUpdateSerialVersionUIDFieldWithoutOverwrite() throws Exception {
        shouldUpdateSerialVersionUIDField(false, false, 7L, 7L);
    }

    private void shouldUpdateSerialVersionUIDField(boolean overwrt, boolean forcethrow, long expectedUIDSerializable, long expectedUIDException) {
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: 'java')

        project.task("serialver", type: InsertSerialVersionUIDTask.class) {
            serialver = '42L'
            overwrite = overwrt
            forceUIDOnException = forcethrow
        }

        createSerializableJavaSourceWithSerialVersionUID(project)
        createExceptionJavaSourceWithSerialVersionUID(project)

        project.compileJava.execute()
        project.serialver.execute()

        assertEquals(expectedUIDSerializable, getCompiledSerialzableClass(project).getField("serialVersionUID").getConstantValue())
        assertEquals(expectedUIDException, getCompiledExceptionClass(project).getField("serialVersionUID").getConstantValue())
    }

    @Test
    public void shouldUpdateSerialVersionUIDFieldDefault() {
        Project project = ProjectBuilder.builder().build()
        project.apply(plugin: 'java')

        project.task("serialver", type: InsertSerialVersionUIDTask.class) {
            serialver = '42L'
        }

        createSerializableJavaSourceWithSerialVersionUID(project)
        createExceptionJavaSourceWithSerialVersionUID(project)

        project.compileJava.execute()
        project.serialver.execute()

        assertEquals(42L, getCompiledSerialzableClass(project).getField("serialVersionUID").getConstantValue())
        assertEquals(42L, getCompiledExceptionClass(project).getField("serialVersionUID").getConstantValue())
    }

    private void createSerializableJavaSource(Project project) {
        FileUtils.forceMkdir(project.sourceSets.main.java.srcDirs[0])

        new File(project.sourceSets.main.java.srcDirs[0], 'SerializableClass.java') <<
                'import java.io.Serializable;\n' +
                'public class SerializableClass implements Serializable {\n' +
                '}'
    }

    private void createExceptionJavaSource(Project project) {
        FileUtils.forceMkdir(project.sourceSets.main.java.srcDirs[0])

        new File(project.sourceSets.main.java.srcDirs[0], 'ExceptionClass.java') <<
                'public class ExceptionClass extends Exception {\n' +
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

    private void createExceptionJavaSourceWithSerialVersionUID(Project project) {
        FileUtils.forceMkdir(project.sourceSets.main.java.srcDirs[0])

        new File(project.sourceSets.main.java.srcDirs[0], 'ExceptionClass.java') <<
                'public class ExceptionClass extends Exception {\n' +
                'private static final long serialVersionUID = 7L; \n' +
                '}'
    }

    private CtClass getCompiledSerialzableClass(Project project) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new FileInputStream(new File(project.sourceSets.main.output[0], 'SerializableClass.class')));
        ctClass
    }

    private CtClass getCompiledExceptionClass(Project project) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new FileInputStream(new File(project.sourceSets.main.output[0], 'ExceptionClass.class')));
        ctClass
    }
}