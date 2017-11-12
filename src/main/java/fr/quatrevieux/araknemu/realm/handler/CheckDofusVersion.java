package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.network.realm.out.BadVersion;
import fr.quatrevieux.araknemu.realm.RealmConfiguration;

/**
 * Check the client version
 */
final public class CheckDofusVersion implements PacketHandler<RealmSession, DofusVersion> {
    final private RealmConfiguration configuration;

    public CheckDofusVersion(RealmConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void handle(RealmSession session, DofusVersion packet) {
        if (packet.version().equals(configuration.clientVersion())) {
            return;
        }

        session.write(new BadVersion(configuration.clientVersion()));
        session.close();
    }

    @Override
    public Class<DofusVersion> packet() {
        return DofusVersion.class;
    }
}
