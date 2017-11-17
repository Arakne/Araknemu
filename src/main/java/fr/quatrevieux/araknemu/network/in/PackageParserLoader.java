package fr.quatrevieux.araknemu.network.in;

import fr.quatrevieux.araknemu.util.loader.ClassExtractorInterface;
import fr.quatrevieux.araknemu.util.loader.JarClassExtractor;
import fr.quatrevieux.araknemu.util.loader.SimpleClassExtractor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;

/**
 * package parser loader for a package
 *
 * @todo https://stackoverflow.com/questions/11012819/how-can-i-get-a-resource-folder-from-inside-my-jar-file
 */
final public class PackageParserLoader implements ParserLoader {
    final private String name;

    public PackageParserLoader(String name) {
        this.name = name;
    }

    @Override
    public Collection<SinglePacketParser> load() throws IOException {
        URI uri;

        try {
            uri = Thread.currentThread().getContextClassLoader().getResource(name.replace('.', '/')).toURI();
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }

        ClassExtractorInterface extractor;

        if (uri.getScheme().contains("jar")) {
            extractor = new JarClassExtractor(name.replace('.', '/'));
        } else {
            extractor = new SimpleClassExtractor(name.replace('.', '/'));
        }

        Collection<SinglePacketParser> parsers = new ArrayList<>();

        for (Class current : extractor.extract()) {
            if (!Packet.class.isAssignableFrom(current)) {
                continue;
            }

            for (Class parserClass: current.getDeclaredClasses()) {
                if (SinglePacketParser.class.isAssignableFrom(parserClass)) {
                    try {
                        parsers.add(
                            SinglePacketParser.class.cast(parserClass.newInstance())
                        );
                    } catch (Exception e) { }
                }
            }
        }

        return parsers;
    }
}
