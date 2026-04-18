package fr.quatrevieux.araknemu.network.game.out.social.enemy;

import fr.quatrevieux.araknemu.network.game.out.social.FriendEnemyErrorCodes;

public final class EnemyAddError {

    private final FriendEnemyErrorCodes error;

    public EnemyAddError(FriendEnemyErrorCodes error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "iAE" + error.code();
    }
}
