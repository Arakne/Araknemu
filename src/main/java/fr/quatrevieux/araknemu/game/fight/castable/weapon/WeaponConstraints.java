package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;

/**
 * Cast constraints for a weapon
 */
final public class WeaponConstraints implements SpellConstraints {
    final private Weapon weapon;

    public WeaponConstraints(Weapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public Interval range() {
        return weapon.info().range();
    }

    @Override
    public boolean lineLaunch() {
        return false;
    }

    @Override
    public boolean lineOfSight() {
        return true;
    }

    @Override
    public boolean freeCell() {
        return false;
    }

    @Override
    public int launchPerTurn() {
        return 0;
    }

    @Override
    public int launchPerTarget() {
        return 0;
    }

    @Override
    public int launchDelay() {
        return 0;
    }

    @Override
    public int[] requiredStates() {
        return new int[] {};
    }

    @Override
    public int[] forbiddenStates() {
        return new int[] {1, 3, 18, 42};
    }
}
