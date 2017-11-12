package fr.quatrevieux.araknemu.core.config;

import org.ini4j.Ini;

/**
 * Driver implementation using ini files
 */
final public class IniDriver implements Driver {
    final private Ini ini;

    public IniDriver(Ini ini) {
        this.ini = ini;
    }

    @Override
    public boolean has(String key) {
        return ini.containsKey(key);
    }

    @Override
    public Object get(String key) {
        return ini.get(key);
    }

    @Override
    public Pool pool(String key) {
        return new IniPool(
            ini.get(key)
        );
    }
}
