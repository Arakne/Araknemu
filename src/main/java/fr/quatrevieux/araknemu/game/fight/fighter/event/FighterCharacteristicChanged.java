package fr.quatrevieux.araknemu.game.fight.fighter.event;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

/**
 * The fighter has changed a characteristic value
 */
final public class FighterCharacteristicChanged {
    final private Characteristic characteristic;
    final private int value;

    public FighterCharacteristicChanged(Characteristic characteristic, int value) {
        this.characteristic = characteristic;
        this.value = value;
    }

    public Characteristic characteristic() {
        return characteristic;
    }

    public int value() {
        return value;
    }
}
