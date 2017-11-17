package fr.quatrevieux.araknemu.util.loader;

import java.io.IOException;
import java.util.List;

/**
 * Extractor for classes
 */
public interface ClassExtractorInterface {
    /**
     * Extract all classes
     *
     * @return List of extracted classes
     *
     * @throws IOException
     */
    public List<Class> extract() throws IOException;
}
