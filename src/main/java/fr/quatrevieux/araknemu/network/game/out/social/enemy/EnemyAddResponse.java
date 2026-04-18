package fr.quatrevieux.araknemu.network.game.out.social.enemy;

public class EnemyAddResponse {
    private final String pseudo;

    public EnemyAddResponse(String pseudo) {
        this.pseudo = pseudo;
    }

    @Override
    public String toString() {
        return "iAK" + pseudo;
    }
}
