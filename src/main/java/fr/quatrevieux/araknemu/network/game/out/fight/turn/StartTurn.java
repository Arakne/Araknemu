package fr.quatrevieux.araknemu.network.game.out.fight.turn;

import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;

/**
 * Start a new turn
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L259
 */
final public class StartTurn {
    final private FightTurn turn;

    public StartTurn(FightTurn turn) {
        this.turn = turn;
    }

    @Override
    public String toString() {
        return "GTS" + turn.fighter().id() + "|" + turn.duration().toMillis();
    }
}
