package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

final public class RemoveInvocationFromTurnList {
    final private PassiveFighter fighter;

    public RemoveInvocationFromTurnList(PassiveFighter fighter) {
        this.fighter = fighter;
    }

    public PassiveFighter fighter() {
        return fighter;
    }
}
