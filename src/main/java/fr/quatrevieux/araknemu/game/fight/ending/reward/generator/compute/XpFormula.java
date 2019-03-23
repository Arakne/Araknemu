package fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Formulas used to compute the win Xp
 */
public interface XpFormula {
    /**
     * Scoped formula for end fight results
     */
    public interface Scope {
        public long compute(Fighter fighter);
    }

    /**
     * Initialize the formula for the given fight results
     */
    public Scope initialize(EndFightResults results);
}
