package fr.quatrevieux.araknemu.network.game.out.fight.turn;

import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;

/**
 * Finish a turn
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L304
 */
final public class FinishTurn {
    final private FightTurn turn;

    public FinishTurn(FightTurn turn) {
        this.turn = turn;
    }

    @Override
    public String toString() {
        return "GTF" + turn.fighter().id();
    }
}
