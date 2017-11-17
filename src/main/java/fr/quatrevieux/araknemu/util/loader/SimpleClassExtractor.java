package fr.quatrevieux.araknemu.util.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Extract classes from simple file list (not in jar)
 */
final public class SimpleClassExtractor implements ClassExtractorInterface {
    final private String path;

    public SimpleClassExtractor(String path) {
        this.path = path;
    }

    @Override
    public List<Class> extract() throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);

        List<Class> classes = new ArrayList<>();

        for (File file : new File(url.getFile()).listFiles()) {
            String filename = file.getName();

            if (!filename.endsWith(".class")) {
                continue;
            }

            filename = path + "/" + filename.substring(0, filename.length() - ".class".length());

            try {
                classes.add(
                    Class.forName(filename.replace('/', '.'))
                );
            } catch (ClassNotFoundException e) { }
        }

        return classes;
    }
}
