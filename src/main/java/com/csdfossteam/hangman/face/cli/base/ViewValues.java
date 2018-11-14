
package com.csdfossteam.hangman.face.cli.base;

import java.util.HashMap;
import java.util.Map;


/**
 * A collection of values to be used in the render of a View.
 * <p>
 * The object to string call is performed on the request of the value.
 *
 * @see View
 *
 * @author Akritas Akritidis
 */
public class ViewValues {

    private final Map<String, Object> values;

    public ViewValues() {
        this.values = new HashMap<>();
    }

    public ViewValues set(String name, Object value) {

        values.put(name, value);
        return this;
    }

    /**
     * Returns the value of the given name, or null if missing.
     */
    public String get(String name) {
        return get(name, null);
    }

    /**
     * Returns the value of the given name, or the given default value if missing.
     * <p>
     * The default value may be null, in that case the returned value can be null.
     */
    public String get(String name, String defaul) {

        final Object object = values.getOrDefault(name, defaul);
        return object == null ? null : object.toString();
    }

    public void unset(String name) {
        values.remove(name);
    }

    /**
     * Removes all the values. Equivalent of creating a new object.
     */
    public void unsetAll() {
        values.clear();
    }

}
