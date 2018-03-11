package fr.quatrevieux.araknemu.network.game.out.spell;

import fr.quatrevieux.araknemu.game.player.spell.SpellBook;

import java.util.stream.Collectors;

/**
 * Send player spells
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Spells.as#L43
 */
final public class SpellList {
    final private SpellBook spells;

    public SpellList(SpellBook spells) {
        this.spells = spells;
    }

    @Override
    public String toString() {
        return "SL" + spells.all()
            .stream()
            .map(entry -> entry.spell().id() + "~" + entry.spell().level() + "~" + entry.position())
            .collect(Collectors.joining(";"))
        ;
    }
}
