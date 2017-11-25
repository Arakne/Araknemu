package fr.quatrevieux.araknemu.realm.host;

/**
 * Host for game server
 */
final public class GameHost {
    public enum State {
        OFFLINE,
        ONLINE,
        SAVING
    }

    final private GameConnector connector;
    final private int id;
    final private int port;
    final private String ip;

    // @todo Quelle valeur ?
    private int completion = 110;
    private boolean canLog = false;
    private State state    = State.OFFLINE;

    public GameHost(GameConnector connector, int id, int port, String ip) {
        this.connector = connector;
        this.id = id;
        this.port = port;
        this.ip = ip;
    }

    /**
     * Get the server ID
     * MUST be present is Dofus langs
     */
    public int id() {
        return id;
    }

    /**
     * Get the server port
     */
    public int port() {
        return port;
    }

    /**
     * Get the server IP address
     */
    public String ip() {
        return ip;
    }

    /**
     * Get the server completion
     */
    public int completion() {
        return completion;
    }

    /**
     * Check if can log into the server
     */
    public boolean canLog() {
        return canLog;
    }

    /**
     * Set if can log into the server
     */
    public void setCanLog(boolean b) {
        canLog = b;
    }

    /**
     * Get the current server state
     */
    public State state() {
        return state;
    }

    /**
     * Set the server state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Get the connector for this host
     */
    public GameConnector connector() {
        return connector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameHost host = (GameHost) o;

        return id == host.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
