package fr.quatrevieux.araknemu.game.world.creature.accessory;

import org.apache.commons.lang3.StringUtils;

/**
 * Base accessories class
 */
abstract public class AbstractAccessories implements Accessories {
    @Override
    public String toString() {
        return StringUtils.join(all(), ",");
    }
}
