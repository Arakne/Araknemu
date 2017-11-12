package fr.quatrevieux.araknemu.core.config;

/**
 * NullObject for Pool
 */
final public class EmptyPool implements Pool {
    @Override
    public boolean has(String key) {
        return false;
    }

    @Override
    public String get(String key) {
        return null;
    }
}
