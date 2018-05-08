package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Result for critical failure for spell cast
 */
final public class CastFailed implements ActionResult {
    final private Fighter caster;
    final private Spell spell;

    public CastFailed(Fighter caster, Spell spell) {
        this.caster = caster;
        this.spell = spell;
    }

    @Override
    public int action() {
        return 302;
    }

    @Override
    public Fighter performer() {
        return caster;
    }

    @Override
    public Object[] arguments() {
        return new Object[] { spell.id() };
    }

    @Override
    public boolean success() {
        return false;
    }
}
