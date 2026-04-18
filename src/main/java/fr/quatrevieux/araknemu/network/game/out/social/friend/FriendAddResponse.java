package fr.quatrevieux.araknemu.network.game.out.social.friend;

public class FriendAddResponse {
    private final String pseudo;


    public FriendAddResponse(String pseudo) {
        this.pseudo = pseudo;
    }

    @Override
    public String toString() {
        return "FAK" + pseudo;
    }
}
