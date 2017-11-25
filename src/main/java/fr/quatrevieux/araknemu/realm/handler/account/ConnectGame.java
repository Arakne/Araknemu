package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.ChooseServer;
import fr.quatrevieux.araknemu.network.realm.out.SelectServerError;
import fr.quatrevieux.araknemu.network.realm.out.SelectServerPlain;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import fr.quatrevieux.araknemu.realm.host.HostService;

/**
 * Connect the client to choose game server
 *
 * @todo Do not use SelectServerPlain
 */
final public class ConnectGame implements PacketHandler<RealmSession, ChooseServer> {
    final private HostService service;

    public ConnectGame(HostService service) {
        this.service = service;
    }

    @Override
    public void handle(RealmSession session, ChooseServer packet) {
        if (!service.isAvailable(packet.id())) {
            session.write(
                new SelectServerError(SelectServerError.Error.CANT_SELECT)
            );

            return;
        }

        GameHost host = service.get(packet.id());

        host.connector().token(
            session.account(),
            token -> {
                session.write(new SelectServerPlain(host.ip(), host.port(), token));
                session.close();
            }
        );
    }

    @Override
    public Class<ChooseServer> packet() {
        return ChooseServer.class;
    }
}
