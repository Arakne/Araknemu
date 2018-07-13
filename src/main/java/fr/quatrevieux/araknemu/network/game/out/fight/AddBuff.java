package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Add buff to fighter
 */
final public class AddBuff {
    final private Buff buff;

    public AddBuff(Buff buff) {
        this.buff = buff;
    }

    @Override
    public String toString() {
        return "GIE"
            + buff.effect().effect() + ";"
            + buff.target().id() + ";"
            + buff.effect().min() + ";"
            + (buff.effect().max() == 0 ? "" : buff.effect().max()) + ";"
            + buff.effect().special() + ";"
            + buff.effect().text() + ";"
            + buff.remainingTurns() + ";"
            + (buff.action() instanceof Spell ? ((Spell) buff.action()).id() : "")
        ;
    }
}
