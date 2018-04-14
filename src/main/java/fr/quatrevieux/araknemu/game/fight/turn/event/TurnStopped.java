package fr.quatrevieux.araknemu.game.fight.turn.event;

import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;

/**
 * The fighter turn is stopped
 */
final public class TurnStopped {
    final private FightTurn turn;

    public TurnStopped(FightTurn turn) {
        this.turn = turn;
    }

    public FightTurn turn() {
        return turn;
    }
}
