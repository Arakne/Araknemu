package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.event.ActionPointsUsed;
import fr.quatrevieux.araknemu.game.fight.turn.event.MovementPointsUsed;

/**
 * Handle fighter turn points (AP / MP)
 */
final public class FighterTurnPoints {
    final private Fight fight;
    final private Fighter fighter;

    private int movementPoints;
    private int actionPoints;

    private int usedMovementPoints;
    private int usedActionPoints;

    public FighterTurnPoints(Fight fight, Fighter fighter) {
        this.fight = fight;
        this.fighter = fighter;

        this.movementPoints = fighter.characteristics().get(Characteristic.MOVEMENT_POINT);
        this.actionPoints = fighter.characteristics().get(Characteristic.ACTION_POINT);
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

    /**
     * Add movement points to the current turn
     */
    public void addMovementPoints(int value) {
        movementPoints += value;
    }

    /**
     * Remove movement points to the current turn
     *
     * @return int The real removed MP amount
     */
    public int removeMovementPoints(int value) {
        if (value > movementPoints()) {
            value = movementPoints();
        }

        movementPoints -= value;

        return value;
    }

    /**
     * Get the current fighter action points
     */
    public int actionPoints() {
        return actionPoints - usedActionPoints;
    }

    /**
     * Remove action points
     *
     * @param points Points to remove
     */
    public void useActionPoints(int points) {
        usedActionPoints += points;

        fight.dispatch(new ActionPointsUsed(fighter, points));
    }

    /**
     * Add action points to the current turn
     */
    public void addActionPoints(int value) {
        actionPoints += value;
    }

    /**
     * Remove action points to the current turn
     *
     * @return int The real removed AP amount
     */
    public int removeActionPoints(int value) {
        if (value > actionPoints()) {
            value = actionPoints();
        }

        actionPoints -= value;

        return value;
    }
}
