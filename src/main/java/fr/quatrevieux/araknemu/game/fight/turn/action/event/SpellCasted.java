package fr.quatrevieux.araknemu.game.fight.turn.action.event;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Spell is successfully cast to the fight
 */
final public class SpellCasted {
    final private Cast action;

    public SpellCasted(Cast action) {
        this.action = action;
    }

    public Cast action() {
        return action;
    }

    public Spell spell() {
        return action.spell();
    }

    public Fighter caster() {
        return action.caster();
    }

    public FightCell target() {
        return action.target();
    }
}
