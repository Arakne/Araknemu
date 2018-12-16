package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Store the character characteristics
 */
public interface CharacterCharacteristics extends Characteristics {
    /**
     * Get the character base stats (i.e. boosted stats + race stats)
     */
    public MutableCharacteristics base();

    /**
     * Get the total stuff stats
     */
    public Characteristics stuff();

    /**
     * Get the feat (candy ??) stats
     */
    public Characteristics feats();

    /**
     * Get the boost (buff) stats
     */
    public Characteristics boost();

    /**
     * Get the available boost points
     */
    public int boostPoints();

    /**
     * Get the current player initiative
     */
    public int initiative();

    /**
     * Get the current player discernment
     */
    public int discernment();

    /**
     * Get the current player pods
     */
    public int pods();
}
