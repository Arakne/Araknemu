package fr.quatrevieux.araknemu.game.fight.ending;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

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
}
