package fr.quatrevieux.araknemu.game.fight.castable;

import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.List;

/**
 * Object which can be casted on a fight
 */
public interface Castable {
    /**
     * List of normal spell effects
     */
    public List<SpellEffect> effects();

    /**
     * List of critical spell effects
     */
    public List<SpellEffect> criticalEffects();

    /**
     * The AP cost
     */
    public int apCost();

    /**
     * Percent of chance for get critical hit
     */
    public int criticalHit();

    /**
     * Percent of chance for get a critical failure
     */
    public int criticalFailure();

    /**
     * Does the castable range is modifiable ?
     */
    public boolean modifiableRange();

    /**
     * Constraints for casting
     */
    public SpellConstraints constraints();
}
