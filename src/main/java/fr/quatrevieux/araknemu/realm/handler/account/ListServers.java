package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.AskServerList;
import fr.quatrevieux.araknemu.network.realm.out.ServerList;
import fr.quatrevieux.araknemu.realm.host.HostService;

/**
 * List servers and characters count per server
 */
final public class ListServers implements PacketHandler<RealmSession, AskServerList> {
    final private HostService service;

    public ListServers(HostService service) {
        this.service = service;
    }

    @Override
    public void handle(RealmSession session, AskServerList packet) {
        session.write(
            new ServerList(
                ServerList.ONE_YEAR, // @todo abo
                service.charactersByHost(session.account())
            )
        );
    }

    @Override
    public Class<AskServerList> packet() {
        return AskServerList.class;
    }
}
