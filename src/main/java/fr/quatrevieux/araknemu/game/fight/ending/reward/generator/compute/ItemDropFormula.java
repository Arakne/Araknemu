package fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Map;

/**
 * Formulas used to compute the dropped items
 */
public interface ItemDropFormula {
    /**
     * Scoped formula for end fight results
     */
    public interface Scope {
        public Map<Integer, Integer> compute(Fighter fighter);
    }

    /**
     * Initialize the formula for the given fight results
     */
    public Scope initialize(EndFightResults results);
}
