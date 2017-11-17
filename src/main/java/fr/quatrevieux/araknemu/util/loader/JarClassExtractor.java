package fr.quatrevieux.araknemu.util.loader;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Extract classes from current JAR file
 */
final public class JarClassExtractor implements ClassExtractorInterface {
    final private String directory;

    public JarClassExtractor(String directory) {
        this.directory = directory;
    }

    @Override
    public List<Class> extract() throws IOException {
        Path path = Paths.get(
            getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toString()
                .substring("file:".length())
        );

        FileSystem fs = FileSystems.newFileSystem(path, null);
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(directory));

        List<Class> classes = new ArrayList<>();

        for (Path p : directoryStream) {
            String filename = p.toString();

            if (!filename.endsWith(".class")) {
                continue;
            }

            filename = filename.substring(1, filename.length() - ".class".length());

            try {
                classes.add(
                    Class.forName(filename.replace('/', '.'))
                );
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }
}
