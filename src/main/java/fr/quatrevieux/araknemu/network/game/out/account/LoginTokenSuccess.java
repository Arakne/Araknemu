package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * Packet for successfully login to game server
 */
final public class LoginTokenSuccess {
    final private int community;

    public LoginTokenSuccess(int community) {
        this.community = community;
    }

    @Override
    public String toString() {
        return "ATK" + community;
    }
}
