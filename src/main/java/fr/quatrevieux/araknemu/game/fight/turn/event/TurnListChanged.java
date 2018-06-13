package fr.quatrevieux.araknemu.game.fight.turn.event;

import fr.quatrevieux.araknemu.game.fight.turn.FightTurnList;

/**
 * The fight turn list has changed
 */
final public class TurnListChanged {
    final private FightTurnList turnList;

    public TurnListChanged(FightTurnList turnList) {
        this.turnList = turnList;
    }

    public FightTurnList turnList() {
        return turnList;
    }
}
