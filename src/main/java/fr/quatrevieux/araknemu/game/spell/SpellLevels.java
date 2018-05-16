package fr.quatrevieux.araknemu.game.spell;

import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.game.spell.adapter.SpellLevelAdapter;

import java.util.ArrayList;
import java.util.List;
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
            throw new NoSuchElementException("Invalid spell level");
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
