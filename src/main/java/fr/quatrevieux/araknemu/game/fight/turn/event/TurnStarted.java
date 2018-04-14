package fr.quatrevieux.araknemu.game.fight.turn.event;

import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;

/**
 * A new turn is started
 */
final public class TurnStarted {
    final private FightTurn turn;

    public TurnStarted(FightTurn turn) {
        this.turn = turn;
    }

    public FightTurn turn() {
        return turn;
    }
}
