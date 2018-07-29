package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Characteristics for a fighter
 */
public interface FighterCharacteristics extends Characteristics {
    /**
     * Get the fighter initiative
     */
    public int initiative();

    /**
     * Change a buff characteristic
     *
     * @param characteristic Characteristic to add
     * @param value The value of the effect. Positive valuer for add the characteristic, or negative to remove
     */
    public void alter(Characteristic characteristic, int value);
}
