package io.github.future0923.debug.tools.server.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

/**
 * @author future0923
 */
public class StringSource extends SimpleJavaFileObject {
    private final String contents;

    public StringSource(String className, String contents) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.contents = contents;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return contents;
    }

}