package fr.quatrevieux.araknemu.game.spell;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.List;

/**
 * Interface for spells
 */
public interface Spell extends Castable {
    /**
     * Get the spell id
     */
    public int id();

    /**
     * Get the spell sprite
     */
    public int spriteId();

    /**
     * Get the spell sprite arguments
     */
    public String spriteArgs();

    /**
     * Get the spell level in interval [1-6]
     */
    public int level();

    /**
     * Does the spell range is modifiable ?
     */
    public boolean modifiableRange();

    /**
     * Minimal player level for use the spell
     */
    public int minPlayerLevel();

    /**
     * Does critical failures will ends the current fight turn ?
     */
    public boolean endsTurnOnFailure();
}
