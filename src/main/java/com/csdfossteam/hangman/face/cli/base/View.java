
package com.csdfossteam.hangman.face.cli.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A text interface with placeholder for values.
 * <p>
 * The format of the based string follows the next rules:
 * <ul>
 * <li>A placeholder is declared by the name of the value that will be placed
 * wrapped around the characters { and }.
 * <li>If there is any whitespace before the start of the name of the value, it
 * will be aligned to the right.
 * <li>If there is any whitespace after the end of the name of the value, it
 * will be aligned to the left.
 * <li>If there is any whitespace before and after the name of the value, it
 * will be aligned to the center (<b>not implemented yet<b>).
 * </ul>
 * <p>
 * Example:
 *
 * <pre>
 * Name: {name             }
 * Age:  {   age} years old
 * </pre>
 *
 * @see ViewValues
 *
 * @author Akritas Akritidis
 */
public class View {

    // TODO define a escape sequence
    // TODO allow for repeating names

    protected final String content;
    protected final String pattern;

    protected final Map<String, Integer> arguments;

    /**
     * Creates a view by parsing the given string.
     */
    public View(String content) {
        this.content = content;

        arguments = new HashMap<>();

        pattern = parse(content, arguments);
    }

    private static String parse(String content, Map<String, Integer> arguments) {
        final StringBuffer sb = new StringBuffer();

        final Pattern pattern = Pattern.compile("\\{(.*)\\}");
        final Matcher matcher = pattern.matcher(content);

        int index = 0;

        while (matcher.find()) {

            final int len = matcher.group(0).length();
            final String key = matcher.group(1);

            final String replacement = parseArgument(arguments, index, len, key);

            matcher.appendReplacement(sb, replacement);

            index++;
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private static String parseArgument(Map<String, Integer> arguments, int index, int len, String keyRaw) {

        final String key = keyRaw.trim();

        if (key.isEmpty()) throw new RuntimeException("Missing placeholder name");
        if (arguments.containsKey(key)) throw new RuntimeException("Duplicate placeholder name");

        arguments.put(key, index);

        if (keyRaw.startsWith(" ")) {
            return "%" + len + "s";
        } else {
            return "%-" + len + "s";
        }

    }

    /**
     * Returns the text representation of the view with the given values.
     */
    public final String text(ViewValues values) {

        return String.format(pattern, args(values));
    }

    protected final Object[] args(ViewValues values) {

        final Object[] args = new Object[arguments.size()];

        for (final Entry<String, Integer> i : arguments.entrySet()) {
            final String name = i.getKey();
            final int index = i.getValue();

            args[index] = values.get(name, "");
        }

        return args;
    }

    /**
     * Prints the text representation of the view with the given values using the
     * given console.
     */
    public void print(Console console, ViewValues values) {

        console.output(text(values));
    }

}
