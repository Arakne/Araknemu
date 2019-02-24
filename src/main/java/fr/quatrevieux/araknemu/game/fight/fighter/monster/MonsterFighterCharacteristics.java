package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

/**
 * Monster fighter characteristics
 */
final public class MonsterFighterCharacteristics implements FighterCharacteristics {
    final private Monster monster;
    final private Fighter fighter;

    final private MutableCharacteristics buffs = new DefaultCharacteristics();

    public MonsterFighterCharacteristics(Monster monster, Fighter fighter) {
        this.monster = monster;
        this.fighter = fighter;
    }

    @Override
    public int initiative() {
        return monster.initiative();
    }

    @Override
    public int get(Characteristic characteristic) {
        return monster.characteristics().get(characteristic) + buffs.get(characteristic);
    }

    @Override
    public void alter(Characteristic characteristic, int value) {
        buffs.add(characteristic, value);
        fighter.dispatch(new FighterCharacteristicChanged(characteristic, value));
    }
}
