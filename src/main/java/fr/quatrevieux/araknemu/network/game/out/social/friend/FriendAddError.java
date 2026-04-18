package fr.quatrevieux.araknemu.network.game.out.social.friend;

import fr.quatrevieux.araknemu.network.game.out.social.FriendEnemyErrorCodes;

public final class FriendAddError {

    private final FriendEnemyErrorCodes error;

    public FriendAddError(FriendEnemyErrorCodes error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "FAE" + error.code();
    }
}
