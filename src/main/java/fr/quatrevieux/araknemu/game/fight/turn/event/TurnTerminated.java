package fr.quatrevieux.araknemu.game.fight.turn.event;

import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;

/**
 * The turn is terminated
 *
 * Unlike TurnStopped, this event is dispatched whether is started or not
 */
final public class TurnTerminated {
    final private FightTurn turn;
    final private boolean aborted;

    public TurnTerminated(FightTurn turn, boolean aborted) {
        this.turn = turn;
        this.aborted = aborted;
    }

    public FightTurn turn() {
        return turn;
    }

    public boolean aborted() {
        return aborted;
    }
}
