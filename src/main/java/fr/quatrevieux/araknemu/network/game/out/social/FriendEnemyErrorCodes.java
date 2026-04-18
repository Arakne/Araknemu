package fr.quatrevieux.araknemu.network.game.out.social;

public enum FriendEnemyErrorCodes {
    NOT_FOUND("f"),
    EGOCENTRIC("y"),
    ALREADY_ADDED("a"),
    FULL_LIST("m");

    private final String code;

    FriendEnemyErrorCodes(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
