package fr.quatrevieux.araknemu.network.in;

import java.io.File;
import java.net.URL;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    public Collection<SinglePacketParser> load() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(name.replace('.', '/'));

        if (url == null) {
            return Collections.EMPTY_LIST;
        }

        Collection<SinglePacketParser> parsers = new ArrayList<>();

        for (File file : new File(url.getFile()).listFiles()) {
            if (!file.getName().endsWith(".class")) {
                continue;
            }

            Class current;

            try {
                current = Class.forName(name + "." + file.getName().substring(0, file.getName().length() - 6));
            } catch (ClassNotFoundException e) {
                continue;
            }

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
