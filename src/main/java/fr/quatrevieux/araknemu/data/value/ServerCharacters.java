package fr.quatrevieux.araknemu.data.value;

/**
 * Value object for get characters count per server
 */
final public class ServerCharacters {
    final private int serverId;
    final private int charactersCount;

    public ServerCharacters(int serverId, int charactersCount) {
        this.serverId = serverId;
        this.charactersCount = charactersCount;
    }

    public int serverId() {
        return serverId;
    }

    public int charactersCount() {
        return charactersCount;
    }
}
