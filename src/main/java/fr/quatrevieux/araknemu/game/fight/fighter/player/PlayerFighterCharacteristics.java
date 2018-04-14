package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerCharacteristics;

/**
 * Player fighter characteristics
 */
final public class PlayerFighterCharacteristics implements FighterCharacteristics {
    final private PlayerCharacteristics baseCharacteristics;

    public PlayerFighterCharacteristics(PlayerCharacteristics baseCharacteristics) {
        this.baseCharacteristics = baseCharacteristics;
    }

    @Override
    public int initiative() {
        return baseCharacteristics.initiative();
    }

    @Override
    public int get(Characteristic characteristic) {
        return baseCharacteristics.get(characteristic);
    }
}
