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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BadVersion that = (BadVersion) o;

        return version != null ? version.equals(that.version) : that.version == null;
    }

    @Override
    public int hashCode() {
        return version != null ? version.hashCode() : 0;
    }
}
