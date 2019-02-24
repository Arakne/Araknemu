package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.apache.commons.lang3.StringUtils;

/**
 * Sprite for monster
 *
 * The sprite type ID MUST be the class id
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L520
 */
final public class MonsterFighterSprite implements Sprite {
    final private MonsterFighter fighter;
    final private Monster monster;

    public MonsterFighterSprite(MonsterFighter fighter, Monster monster) {
        this.fighter = fighter;
        this.monster = monster;
    }

    @Override
    public int id() {
        return fighter.id();
    }

    @Override
    public int cell() {
        return fighter.cell().id();
    }

    @Override
    public Direction orientation() {
        return fighter.orientation();
    }

    @Override
    public Type type() {
        return Type.MONSTER;
    }

    @Override
    public String name() {
        return Integer.toString(monster.id());
    }

    @Override
    public String toString() {
        return
            cell() + ";" +
            fighter.orientation().ordinal() + ";" +
            "0;" + // @todo Bonus value (get from group ?)
            id() + ";" +
            name() + ";" +
            type().id() + ";" +
            monster.gfxId() + "^100;" + // @todo size
            monster.gradeNumber() + ";" +
            StringUtils.join(monster.colors().toHexArray(), ";") + ";" +
            "0,0,0,0;" + // @todo accessories
            fighter.life().current() + ";" +
            fighter.characteristics().get(Characteristic.ACTION_POINT) + ";" +
            fighter.characteristics().get(Characteristic.MOVEMENT_POINT) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_NEUTRAL) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_EARTH) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_FIRE) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_WATER) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_AIR) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_ACTION_POINT) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_MOVEMENT_POINT) + ";" +
            fighter.team().number()
        ;
    }
}
