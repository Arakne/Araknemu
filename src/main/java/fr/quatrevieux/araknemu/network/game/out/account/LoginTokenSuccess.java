package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * Packet for successfully login to game server
 */
final public class LoginTokenSuccess {
    final private String key;

    public LoginTokenSuccess(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "ATK" + key;
    }
}
