/*
 *  *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2015 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 *  * $$ACTIVEEON_INITIAL_DEV$$
 */
package serialver;

import com.darylteo.gradle.javassist.transformers.ClassTransformer;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.build.JavassistBuildException;

import java.io.Serializable;


public class SerialVersionUIDTransformer extends ClassTransformer {

    private static final String SERIALVERSIONUID_FIELD_NAME = "serialVersionUID";

    private long serialVersionUIDValue;

    private boolean overwrite;

    public SerialVersionUIDTransformer(long serialVersionUIDValue) {
        this(serialVersionUIDValue, true);
    }

    public SerialVersionUIDTransformer(long serialVersionUIDValue, boolean overwrite) {
        this.serialVersionUIDValue = serialVersionUIDValue;
        this.overwrite = overwrite;
    }

    public void applyTransformations(CtClass clazz) throws JavassistBuildException {
        try {

            if (hasSerialVersionUIDField(clazz)) {
                if (overwrite) {
                    // replace existing serialVersionUID
                    clazz.removeField(clazz.getField(SERIALVERSIONUID_FIELD_NAME));
                }
            }

            if (!hasSerialVersionUIDField(clazz)) {
                CtField field = new CtField(CtClass.longType, SERIALVERSIONUID_FIELD_NAME, clazz);
                field.setModifiers(javassist.Modifier.STATIC | javassist.Modifier.PRIVATE |
                        javassist.Modifier.FINAL);
                clazz.addField(field, javassist.CtField.Initializer.constant(serialVersionUIDValue));
            }
        } catch (Exception e) {
            throw new JavassistBuildException(e);
        }
    }

    public boolean shouldTransform(CtClass clazz) throws JavassistBuildException {
        try {
            return isClass(clazz) && isSerializable(clazz);
        } catch (NotFoundException e) {
            throw new JavassistBuildException(e);
        }
    }

    private boolean isClass(CtClass clazz) {
        return !clazz.isInterface() && !clazz.isEnum();
    }

    private boolean isSerializable(CtClass clazz) throws NotFoundException {
        return clazz.subtypeOf(ClassPool.getDefault().get(Serializable.class.getName()));
    }

    private boolean hasSerialVersionUIDField(CtClass clazz) {
        try {
            CtField serialVersionUIDField = clazz.getField(SERIALVERSIONUID_FIELD_NAME);
            return serialVersionUIDField.getDeclaringClass().equals(clazz);
        } catch (NotFoundException classHasNoSerialVersionUIDField) {
            return false;
        }
    }
}
