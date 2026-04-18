package fr.quatrevieux.araknemu.network.game.out.social.enemy;

public class EnemyRemoveResponse {
    private final boolean success;

    public EnemyRemoveResponse(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() { return "iD" + (success ? "K" : "Ef"); }
}
