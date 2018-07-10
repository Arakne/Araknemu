package fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat;

import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.List;

/**
 * Result for successfully weapon cast
 */
final public class CloseCombatSuccess implements ActionResult {
    final private Fighter caster;
    final private CastableWeapon weapon;
    final private FightCell target;
    final private boolean critical;

    public CloseCombatSuccess(Fighter caster, CastableWeapon weapon, FightCell target, boolean critical) {
        this.caster = caster;
        this.weapon = weapon;
        this.target = target;
        this.critical = critical;
    }

    @Override
    public int action() {
        return ActionType.CLOSE_COMBAT.id();
    }

    @Override
    public Fighter performer() {
        return caster;
    }

    @Override
    public Object[] arguments() {
        // @todo custom visual effect : target,sprite,displayType,isFrontOfSprite
        return new Object[] { target.id() };
    }

    @Override
    public boolean success() {
        return true;
    }

    /**
     * Is a critical hit ?
     */
    public boolean critical() {
        return critical;
    }

    /**
     * Get the spell effects
     */
    public List<SpellEffect> effects() {
        return critical ? weapon.criticalEffects() : weapon.effects();
    }
}
