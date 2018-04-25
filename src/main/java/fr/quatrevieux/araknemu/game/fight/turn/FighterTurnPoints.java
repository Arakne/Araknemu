package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.event.MovementPointsUsed;

/**
 * Handle fighter turn points (AP / MP)
 */
final public class FighterTurnPoints {
    final private Fight fight;
    final private Fighter fighter;

    final private int movementPoints;

    private int usedMovementPoints;

    public FighterTurnPoints(Fight fight, Fighter fighter) {
        this.fight = fight;
        this.fighter = fighter;

        this.movementPoints = fighter.characteristics().get(Characteristic.MOVEMENT_POINT);
    }

    /**
     * Get the current fighter movement points
     */
    public int movementPoints() {
        return movementPoints - usedMovementPoints;
    }

    /**
     * Remove movement points
     *
     * @param points Points to remove
     */
    public void useMovementPoints(int points) {
        usedMovementPoints += points;

        fight.dispatch(new MovementPointsUsed(fighter, points));
    }
}
