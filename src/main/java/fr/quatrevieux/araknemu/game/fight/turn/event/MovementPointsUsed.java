package fr.quatrevieux.araknemu.game.fight.turn.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Movement points are used for perform an action
 */
final public class MovementPointsUsed {
    final private Fighter fighter;
    final private int quantity;

    public MovementPointsUsed(Fighter fighter, int quantity) {
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
