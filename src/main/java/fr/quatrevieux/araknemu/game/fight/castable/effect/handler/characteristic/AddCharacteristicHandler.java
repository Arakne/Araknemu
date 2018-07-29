package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;

/**
 * Buff effect for adding characteristic points
 */
public class AddCharacteristicHandler extends AlterCharacteristicHandler {
    public AddCharacteristicHandler(Fight fight, Characteristic characteristic) {
        super(fight, characteristic);
    }

    @Override
    protected int value(Buff buff) {
        return buff.effect().min();
    }
}
