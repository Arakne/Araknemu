package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.player.characteristic.CharacterCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Player fighter characteristics
 */
final public class PlayerFighterCharacteristics implements FighterCharacteristics, CharacterCharacteristics {
    final private CharacterCharacteristics baseCharacteristics;
    final private PlayerFighter fighter;

    final private MutableCharacteristics buffs = new DefaultCharacteristics();

    public PlayerFighterCharacteristics(CharacterCharacteristics baseCharacteristics, PlayerFighter fighter) {
        this.baseCharacteristics = baseCharacteristics;
        this.fighter = fighter;
    }

    @Override
    public int initiative() {
        return baseCharacteristics.initiative();
    }

    @Override
    public int get(Characteristic characteristic) {
        return baseCharacteristics.get(characteristic) + buffs.get(characteristic);
    }

    @Override
    public MutableCharacteristics base() {
        return baseCharacteristics.base();
    }

    @Override
    public Characteristics stuff() {
        return baseCharacteristics.stuff();
    }

    @Override
    public Characteristics feats() {
        return baseCharacteristics.feats();
    }

    @Override
    public Characteristics boost() {
        return buffs;
    }

    @Override
    public int boostPoints() {
        return baseCharacteristics.boostPoints();
    }

    @Override
    public int discernment() {
        // @todo Add buff discernment
        return baseCharacteristics.discernment();
    }

    @Override
    public void alter(Characteristic characteristic, int value) {
        buffs.add(characteristic, value);
        fighter.dispatch(new FighterCharacteristicChanged(characteristic, value));
    }
}
