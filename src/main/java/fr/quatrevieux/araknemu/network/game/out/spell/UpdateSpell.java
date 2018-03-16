package fr.quatrevieux.araknemu.network.game.out.spell;

import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import fr.quatrevieux.araknemu.util.Base64;

/**
 * Update the spell data after upgrade
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Spells.as#L31
 */
final public class UpdateSpell {
    final private SpellBookEntry entry;

    public UpdateSpell(SpellBookEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "SUK" + entry.spell().id() + "~" + entry.spell().level() + "~" + Base64.chr(entry.position());
    }
}
