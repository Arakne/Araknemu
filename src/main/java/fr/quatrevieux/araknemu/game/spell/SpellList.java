package fr.quatrevieux.araknemu.game.spell;

/**
 * List of spells
 */
public interface SpellList {
    /**
     * Get one spell by its id
     *
     * @param spellId The spell id
     */
    public Spell get(int spellId);

    /**
     * Check if the creature are the spell
     *
     * @param spellId ID of the spell to check
     */
    public boolean has(int spellId);
}
