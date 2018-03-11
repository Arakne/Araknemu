package fr.quatrevieux.araknemu.game.spell.adapter;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;

/**
 * Adapter {@link SpellConstraints} from {@link fr.quatrevieux.araknemu.data.world.entity.SpellTemplate.Level}
 */
final public class SpellLevelConstraintAdapter implements SpellConstraints {
    final private SpellTemplate.Level level;

    public SpellLevelConstraintAdapter(SpellTemplate.Level level) {
        this.level = level;
    }

    @Override
    public Interval range() {
        return level.range();
    }

    @Override
    public boolean lineLaunch() {
        return level.lineLaunch();
    }

    @Override
    public boolean lineOfSight() {
        return level.lineOfSight();
    }

    @Override
    public boolean freeCell() {
        return level.freeCell();
    }

    @Override
    public int launchPerTurn() {
        return level.launchPerTurn();
    }

    @Override
    public int launchPerTarget() {
        return level.launchPerTarget();
    }

    @Override
    public int launchDelay() {
        return level.launchDelay();
    }

    @Override
    public int[] requiredStates() {
        return level.requiredStates();
    }

    @Override
    public int[] forbiddenStates() {
        return level.forbiddenStates();
    }
}
