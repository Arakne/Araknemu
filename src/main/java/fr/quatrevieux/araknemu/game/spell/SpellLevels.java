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

    final private List<Spell> levels = new ArrayList<>();

    public SpellLevels(SpellTemplate entity) {
        this.entity = entity;

        makeLevels();
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

        if (level < 0 || level >= levels.size()) {
            throw new NoSuchElementException("Invalid spell level");
        }

        return levels.get(level);
    }

    /**
     * Get the maximum spell level
     */
    public int max() {
        return levels.size();
    }

    private void makeLevels() {
        for (int i = 0; i < entity.levels().length; ++i) {
            SpellTemplate.Level level = entity.levels()[i];

            if (level == null) {
                break;
            }

            levels.add(new SpellLevelAdapter(i + 1, entity, level));
        }
    }
}
