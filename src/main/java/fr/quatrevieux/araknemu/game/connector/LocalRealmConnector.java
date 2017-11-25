package fr.quatrevieux.araknemu.game.connector;

import fr.quatrevieux.araknemu.realm.host.GameHost;
import fr.quatrevieux.araknemu.realm.host.HostService;
import fr.quatrevieux.araknemu.realm.host.LocalGameConnector;

/**
 * GameConnector for local game server (i.e. Game and Realm on same application)
 */
final public class LocalRealmConnector implements RealmConnector {
    final private HostService realm;
    final private ConnectorService service;

    public LocalRealmConnector(HostService realm, ConnectorService service) {
        this.realm = realm;
        this.service = service;
    }

    @Override
    public void declare(int id, int port, String ip) {
        realm.declare(
            new GameHost(
                new LocalGameConnector(service),
                id,
                port,
                ip
            )
        );
    }

    @Override
    public void updateState(int id, GameHost.State state, boolean canLog) {
        realm.updateHost(id, state, canLog);
    }
}
