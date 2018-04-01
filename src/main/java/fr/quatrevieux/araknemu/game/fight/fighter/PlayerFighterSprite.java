package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.apache.commons.lang3.StringUtils;

/**
 * Sprite for fighter
 *
 * The sprite type ID MUST be the class id
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L764
 */
final public class PlayerFighterSprite implements Sprite {
    final private PlayerFighter fighter;

    public PlayerFighterSprite(PlayerFighter fighter) {
        this.fighter = fighter;
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
    public Type type() {
        return Type.PLAYER;
    }

    @Override
    public String name() {
        return fighter.player().name();
    }

    @Override
    public String toString() {
        return
            cell() + ";" +
            "1;" + // @todo direction
            "0;" + // Bonus value, not used on player
            id() + ";" +
            name() + ";" +
            fighter.player().race().race().ordinal() + ";" +
            fighter.player().gfxId() + ";" +
            fighter.player().sex().ordinal() + ";" +
            fighter.player().experience().level() + ";" +
            "0,0,0,0;" + // @todo alignment
            StringUtils.join(fighter.player().colors().toHexArray(), ";") + ";" +
            fighter.player().inventory().accessories() + ";" +
            fighter.currentLife() + ";" +
            fighter.characteristics().get(Characteristic.ACTION_POINT) + ";" +
            fighter.characteristics().get(Characteristic.MOVEMENT_POINT) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_NEUTRAL) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_EARTH) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_FIRE) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_WATER) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_PERCENT_AIR) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_ACTION_POINT) + ";" +
            fighter.characteristics().get(Characteristic.RESISTANCE_MOVEMENT_POINT) + ";" +
            fighter.team().number() + ";" +
            ";" // @todo mount
        ;
    }
}
