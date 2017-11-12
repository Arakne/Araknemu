package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.AskServerList;
import fr.quatrevieux.araknemu.network.realm.out.ServerList;

/**
 * List servers and characters count per server
 */
final public class ListServers implements PacketHandler<RealmSession, AskServerList> {
    @Override
    public void handle(RealmSession session, AskServerList packet) {
        session.write(
            new ServerList(ServerList.ONE_YEAR)
        );
    }

    @Override
    public Class<AskServerList> packet() {
        return AskServerList.class;
    }
}
