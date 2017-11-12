package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketParser;

/**
 * The dofus client version
 */
final public class DofusVersion implements Packet {
    final private String version;

    public DofusVersion(String version) {
        this.version = version;
    }

    public String version() {
        return version;
    }

    static public PacketParser parser() {
        return DofusVersion::new;
    }
}
