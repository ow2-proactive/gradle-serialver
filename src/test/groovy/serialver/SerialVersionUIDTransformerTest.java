package serialver;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Test;
import serialver.tests.ExceptionSubClass;
import serialver.tests.Interface;
import serialver.tests.NotSerializableClass;
import serialver.tests.SerialVersionUIDAlreadyDefined;
import serialver.tests.SerializableClass;
import serialver.tests.SerializableClassWithInheritance;

import static org.junit.Assert.*;


public class SerialVersionUIDTransformerTest {

    @Test
    public void shouldTransform_ClassIsSerializable() throws Exception {
        assertTrue(new SerialVersionUIDTransformer(42L).shouldTransform(getClazz(SerializableClass.class)));

        assertTrue(new SerialVersionUIDTransformer(42L)
                .shouldTransform(getClazz(SerializableClassWithInheritance.class)));

        assertTrue(new SerialVersionUIDTransformer(42L)
          .shouldTransform(getClazz(ExceptionSubClass.class)));
    }

    @Test
    public void shouldTransform_SerialVersionUIDAlreadyDefined() throws Exception {
        assertTrue(new SerialVersionUIDTransformer(42L).shouldTransform(
          getClazz(SerialVersionUIDAlreadyDefined.class)));
    }

    @Test
    public void shouldNotTransform_ClassIsNotSerializable() throws Exception {
        assertFalse(new SerialVersionUIDTransformer(42L).shouldTransform(getClazz(NotSerializableClass.class)));
    }

    @Test
    public void shouldNotTransform_Interface() throws Exception {
        assertFalse(new SerialVersionUIDTransformer(42L).shouldTransform(getClazz(Interface.class)));
    }

    private CtClass getClazz(Class clazz) throws NotFoundException {
        return ClassPool.getDefault().get(clazz.getName());
    }
}