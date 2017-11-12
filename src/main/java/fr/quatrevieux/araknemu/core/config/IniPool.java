package fr.quatrevieux.araknemu.core.config;

import org.ini4j.Profile;

/**
 * Configuration Pool for ini
 */
final public class IniPool implements Pool{
    final private Profile.Section section;

    public IniPool(Profile.Section section) {
        this.section = section;
    }

    @Override
    public boolean has(String key) {
        return section.containsKey(key);
    }

    @Override
    public String get(String key) {
        return section.get(key);
    }
}
