package fr.quatrevieux.araknemu.network.game.out.social.friend;

public class FriendRemoveResponse {
    private final boolean success;

    public FriendRemoveResponse(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() { return "FD" + (success ? "K" : "Ef"); }
}
