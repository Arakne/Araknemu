package fr.quatrevieux.araknemu.game.spell;

/**
 * List of spells
 */
public interface SpellList extends Iterable<Spell> {
    /**
     * Get one spell by its id
     * {@link SpellList#has(int)} must be called before, and returns true. If not undefined behavior can occurs
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
