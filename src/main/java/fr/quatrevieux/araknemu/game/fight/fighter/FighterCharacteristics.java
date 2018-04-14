package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Characteristics for a fighter
 */
public interface FighterCharacteristics extends Characteristics {
    /**
     * Get the fighter initiative
     */
    public int initiative();
}
