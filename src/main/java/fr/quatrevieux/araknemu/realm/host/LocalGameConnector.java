package fr.quatrevieux.araknemu.realm.host;

import fr.quatrevieux.araknemu.game.connector.ConnectorService;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;

/**
 * GameConnector for local game server
 */
final public class LocalGameConnector implements GameConnector {
    final private ConnectorService service;

    public LocalGameConnector(ConnectorService service) {
        this.service = service;
    }

    @Override
    public void checkLogin(AuthenticationAccount account, HostResponse<Boolean> response) {
        response.response(
            service.isLogged(account.id())
        );
    }

    @Override
    public void token(AuthenticationAccount account, HostResponse<String> response) {
        // @todo Gérer token
        response.response("azertyuiop");
    }
}
