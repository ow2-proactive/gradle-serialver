/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package serialver;

import static org.junit.Assert.*;

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


public class SerialVersionUIDTransformerTest {

    @Test
    public void shouldTransform_ClassIsSerializable() throws Exception {
        assertTrue(new SerialVersionUIDTransformer(42L).shouldTransform(getClazz(SerializableClass.class)));

        assertTrue(new SerialVersionUIDTransformer(42L).shouldTransform(getClazz(SerializableClassWithInheritance.class)));

        assertTrue(new SerialVersionUIDTransformer(42L).shouldTransform(getClazz(ExceptionSubClass.class)));
    }

    @Test
    public void shouldTransform_SerialVersionUIDAlreadyDefined() throws Exception {
        assertTrue(new SerialVersionUIDTransformer(42L).shouldTransform(getClazz(SerialVersionUIDAlreadyDefined.class)));
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
