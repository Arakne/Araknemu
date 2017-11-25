package fr.quatrevieux.araknemu.game.connector;

import fr.quatrevieux.araknemu.realm.host.GameHost;

/**
 * GameConnector between game server and realm server
 */
public interface RealmConnector {
    /**
     * Declare sgame host to the realm
     * @param id Server ID
     * @param port Host port
     * @param ip Host IP address
     */
    public void declare(int id, int port, String ip);

    /**
     * Update the game server state
     *
     * @param id The server ID
     * @param state The server state
     * @param canLog A player can log into server ?
     */
    public void updateState(int id, GameHost.State state, boolean canLog);
}
