package fr.quatrevieux.araknemu.game.fight;

/**
 * List of join fight errors
 */
public enum JoinFightError {
    CHALLENGE_FULL('c'),
    TEAM_FULL('t'),
    TEAM_DIFFERENT_ALIGNMENT('a'),
    CANT_DO_BECAUSE_GUILD('g'),
    CANT_DO_TOO_LATE('l'),
    CANT_U_ARE_MUTANT('m'),
    CANT_BECAUSE_MAP('p'),
    CANT_BECAUSE_ON_RESPAWN('r'),
    CANT_YOU_R_BUSY('o'),
    CANT_YOU_OPPONENT_BUSY('z'),
    CANT_FIGHT('h'),
    CANT_FIGHT_NO_RIGHTS('i'),
    EXPIRED_SUBSCRIPTION('s'),
    SUBSCRIPTION_OUT('n'),
    A_NOT_SUBSCRIBED('b'),
    TEAM_CLOSED('f'),
    NO_ZOMBIE_ALLOWED('d');

    final private char error;

    JoinFightError(char error) {
        this.error = error;
    }

    public char error() {
        return error;
    }
}
