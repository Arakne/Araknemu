package fr.quatrevieux.araknemu.game.fight.ending;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;

import java.util.List;

/**
 * Result for fight end
 */
final public class EndFightResults {
    final private Fight fight;
    final private List<Fighter> winners;
    final private List<Fighter> loosers;

    public EndFightResults(Fight fight, List<Fighter> winners, List<Fighter> loosers) {
        this.fight = fight;
        this.winners = winners;
        this.loosers = loosers;
    }

    /**
     * Get the fight
     */
    public Fight fight() {
        return fight;
    }

    /**
     * Get the winner team
     */
    public List<Fighter> winners() {
        return winners;
    }

    /**
     * Get the loosers
     */
    public List<Fighter> loosers() {
        return loosers;
    }

    /**
     * Apply the operation to all looser fighters
     *
     * @param <O> The operation type
     *
     * @return The given operation
     *
     * @see Fighter#apply(FighterOperation)
     */
    public <O extends FighterOperation> O applyToLoosers(O operation) {
        loosers.forEach(fighter -> fighter.apply(operation));

        return operation;
    }

    /**
     * Apply the operation to all winner fighters
     *
     * @param <O> The operation type
     *
     * @return The given operation
     *
     * @see Fighter#apply(FighterOperation)
     */
    public <O extends FighterOperation> O applyToWinners(O operation) {
        winners.forEach(fighter -> fighter.apply(operation));

        return operation;
    }
}
