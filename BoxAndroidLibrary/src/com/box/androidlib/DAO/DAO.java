/*******************************************************************************
 * Copyright 2011 Box.net.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.box.androidlib.DAO;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;

/**
 * Base DAO class.
 * 
 * @author developers@box.net
 */
public abstract class DAO {

    /**
     * Used to track which DAOs have been toStringed already.
     */
    private final HashSet<Integer> hashCodesToStringed = new HashSet<Integer>();

    /**
     * Get a formatted output of the object. Useful for development, but <b>you
     * should never execute this in a final release</b>.
     * 
     * @return A formatted string with member variables.
     */
    @Override
    public final String toString() {
        return toString(0);
    }

    /**
     * Not meant to be called manually. Get a formatted output of the object.
     * Useful for development, but <b>you should never execute this in a final
     * release</b>.
     * 
     * @param indents
     *            The number of indents to prepend to every line
     * @return A formatted string with member variables.
     */
    public final String toString(final int indents) {
        // if DAOs ever refer to each other, we would get an infinite recursion
        // without this check
        if (hashCodesToStringed.contains(hashCode())) {
            return "Skipped to avoid infinite recursion";
        }
        hashCodesToStringed.add(hashCode());

        final StringBuffer sb = new StringBuffer();
        String newLine = "\n    ";
        for (int i = 0; i < indents; i++) {
            newLine = newLine + "    ";
        }
        sb.append(getClass().getName());
        for (final Field field : getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            final StringBuffer value = new StringBuffer();
            final String name = field.getName();
            try {
                if (field.get(this) instanceof DAO) {
                    final DAO dao = (DAO) field.get(this);
                    value.append(dao.toString(indents + 1));
                } else if (field.get(this) instanceof List) {
                    @SuppressWarnings("unchecked")
                    final List<Object> list = (List<Object>) field.get(this);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) instanceof DAO) {
                            final DAO dao = (DAO) list.get(i);
                            value.append(newLine + "    ");
                            value.append(dao.toString(indents + 2));
                        } else {
                            value.append(newLine);
                            value.append(list.get(i).toString());
                        }
                    }
                } else {
                    value.append(String.valueOf(field.get(this)));
                }
            } catch (final IllegalAccessException e) {
                value.append("CANNOT ACCESS PRIVATE MEMBERS");
            }
            sb.append(newLine);
            sb.append(name);
            sb.append(" => ");
            sb.append(value);
        }

        return sb.toString();
    }
}
