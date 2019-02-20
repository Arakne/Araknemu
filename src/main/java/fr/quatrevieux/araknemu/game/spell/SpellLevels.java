package fr.quatrevieux.araknemu.game.spell;

import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;

import java.util.NoSuchElementException;

/**
 * Wrap spell levels
 */
final public class SpellLevels {
    final private SpellTemplate entity;
    final private Spell[] levels;

    public SpellLevels(SpellTemplate entity, Spell[] levels) {
        this.entity = entity;
        this.levels = levels;
    }

    /**
     * Get the spell id
     */
    public int id() {
        return entity.id();
    }

    /**
     * Get the spell name
     */
    public String name() {
        return entity.name();
    }

    /**
     * Get spell at level
     */
    public Spell level(int level) {
        --level;

        if (level < 0 || level >= levels.length) {
            throw new NoSuchElementException("Invalid level " + (level+1) + " for spell " + entity.id());
        }

        return levels[level];
    }

    /**
     * Get the maximum spell level
     */
    public int max() {
        return levels.length;
    }
}
