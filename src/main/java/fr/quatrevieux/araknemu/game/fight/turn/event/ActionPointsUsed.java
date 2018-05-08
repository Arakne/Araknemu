package fr.quatrevieux.araknemu.game.fight.turn.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Action points are used for perform an action
 */
final public class ActionPointsUsed {
    final private Fighter fighter;
    final private int quantity;

    public ActionPointsUsed(Fighter fighter, int quantity) {
        this.fighter = fighter;
        this.quantity = quantity;
    }

    public Fighter fighter() {
        return fighter;
    }

    public int quantity() {
        return quantity;
    }
}
