package fr.quatrevieux.araknemu.network.realm.out;

/**
 * Invalid dofus client version
 */
final public class BadVersion {
    final private String version;

    public BadVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "AlEv" + version;
    }
}
