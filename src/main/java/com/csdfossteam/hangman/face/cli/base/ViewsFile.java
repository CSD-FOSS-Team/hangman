
package com.csdfossteam.hangman.face.cli.base;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A collection of views from a single file.
 * <p>
 * The format of the file follows the next rules:
 * <ul>
 * <li>A view cannot contain a tab character.
 * <li>View are separated by a name decelerations, a deceleration is a line
 * staring with a tab character followed by the name of the view that follows.
 * <li>The format of {@link View} applies for the views here also.
 * </ul>
 * <p>
 * Example:
 *
 * <pre>
 *     Info
 * Name: {name             }
 * Age:  {   age} years old
 *     Message
 * >> {        message        } <<
 * </pre>
 *
 * @see View
 *
 * @author Akritas Akritidis
 */
public class ViewsFile {

    /**
     * Creates a new collection form the file of the given path.
     *
     * @throws IOException
     */
    public static final ViewsFile fromPath(Path path) throws IOException {

        final String text = new String(Files.readAllBytes(path));
        return new ViewsFile(text);
    }

    /**
     * Creates a new collection form the file of the given path.
     *
     * @throws IOException
     */
    public static final ViewsFile fromResoure(String path) throws IOException {

        final InputStream stream = ViewsFile.class.getClassLoader().getResourceAsStream(path);
        try (final Scanner s = new Scanner(stream)) {

            s.useDelimiter("\\A");
            final String text = s.hasNext() ? s.next() : "";

            return new ViewsFile(text);
        }
    }

    protected final HashMap<String, View> views;

    /**
     * Creates a new collection by parsing the given string.
     */
    public ViewsFile(String content) {
        views = new HashMap<>();

        // parse the content

        final Pattern pattern = Pattern.compile("\t(.*)\n([^\t]*)");
        final Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {

            final String name = matcher.group(1);
            final String text = matcher.group(2);

            views.put(name, new View(text));
        }

    }

    /**
     * Get a view based on the given name.
     */
    public View get(String name) {

        return views.get(name);
    }

}
