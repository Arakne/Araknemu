package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.List;

/**
 * Result for successfully spell cast
 */
final public class CastSuccess implements ActionResult {
    final private Fighter caster;
    final private Spell spell;
    final private FightCell target;
    final private boolean critical;

    public CastSuccess(Fighter caster, Spell spell, FightCell target, boolean critical) {
        this.caster = caster;
        this.spell = spell;
        this.target = target;
        this.critical = critical;
    }

    @Override
    public int action() {
        return ActionType.CAST.id();
    }

    @Override
    public Fighter performer() {
        return caster;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {
            spell.id(), target.id(), spell.spriteId(), spell.level(), spell.spriteArgs()
        };
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
        return critical ? spell.criticalEffects() : spell.effects();
    }
}
