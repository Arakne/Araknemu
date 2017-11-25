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
        ServerList out = new ServerList(ServerList.ONE_YEAR);

        // @todo Send characters list
        out.add(new ServerList.Server(0, 0));

        session.write(out);
    }

    @Override
    public Class<AskServerList> packet() {
        return AskServerList.class;
    }
}
