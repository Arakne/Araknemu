package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;

/**
 * Entry for the spell book
 */
final public class SpellBookEntry {
    final private PlayerSpell entity;
    final private SpellLevels spell;

    public SpellBookEntry(PlayerSpell entity, SpellLevels spell) {
        this.entity = entity;
        this.spell = spell;
    }

    /**
     * Get the spell
     */
    public Spell spell() {
        return spell.level(entity.level());
    }

    /**
     * Get the spell position
     */
    public char position() {
        return entity.position();
    }

    /**
     * This entry is a class spell ?
     */
    public boolean classSpell() {
        return entity.classSpell();
    }
}
