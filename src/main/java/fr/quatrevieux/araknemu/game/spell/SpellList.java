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
}
